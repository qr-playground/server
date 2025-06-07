# Provider 설정
provider "aws" {
  region = "ap-northeast-2"
}

# VPC 생성
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "qrworld-vpc"
  }
}

# 인터넷 게이트웨이
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "qrworld-igw"
  }
}

# Public 서브넷
resource "aws_subnet" "public_1" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.3.0/24"
  availability_zone       = "ap-northeast-2a"
  map_public_ip_on_launch = true

  tags = {
    Name = "qrworld-public-1"
  }
}

# 두 번째 Public 서브넷
resource "aws_subnet" "public_2" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.4.0/24"
  availability_zone       = "ap-northeast-2c"
  map_public_ip_on_launch = true

  tags = {
    Name = "qrworld-public-2"
  }
}

# Public 라우팅 테이블
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "qrworld-public-rt"
  }
}

# Public 서브넷 라우팅 연결
resource "aws_route_table_association" "public_1" {
  subnet_id      = aws_subnet.public_1.id
  route_table_id = aws_route_table.public.id
}

# Public 서브넷 라우팅 연결 (두 번째 서브넷용)
resource "aws_route_table_association" "public_2" {
  subnet_id      = aws_subnet.public_2.id
  route_table_id = aws_route_table.public.id
}

# 보안 그룹 - EC2
resource "aws_security_group" "ec2" {
  name        = "qrworld-ec2-sg"
  description = "Security group for EC2"
  vpc_id      = aws_vpc.main.id

  # Spring Boot 애플리케이션용 8080 포트
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow Spring Boot application"
  }

  # SSH 접속
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Prometheus
  ingress {
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Grafana
  ingress {
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # 아웃바드 트래픽 허용
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "qrworld-ec2-sg"
  }
}

# IAM 역할 생성
resource "aws_iam_role" "ec2_role" {
  name = "qrworld-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

# SSM 관리형 정책 연결
resource "aws_iam_role_policy_attachment" "ssm_policy" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

# ECR 정책 추가
resource "aws_iam_role_policy" "ecr_policy" {
  name = "ecr-policy"
  role = aws_iam_role.ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecr:GetAuthorizationToken",
          "ecr:BatchCheckLayerAvailability",
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage"
        ]
        Resource = "*"
      }
    ]
  })
}

# EC2용 인스턴스 프로파일 생성
resource "aws_iam_instance_profile" "ec2_profile" {
  name = "qrworld-ec2-profile"
  role = aws_iam_role.ec2_role.name
}

# EC2 인스턴스
resource "aws_instance" "app" {
  ami           = "ami-0e9bfdb247cc8de84"  # Ubuntu 22.04 LTS AMI
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public_1.id

  # 세부 모니터링 활성화
  monitoring = true

  vpc_security_group_ids = [aws_security_group.ec2.id]
  key_name              = "qrworld-key"
  iam_instance_profile  = aws_iam_instance_profile.ec2_profile.name

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
  }

  user_data = <<-EOF
              #!/bin/bash
              set -e  # 오류 발생 시 스크립트 중단

              # SSM Agent 설치
              sudo snap install amazon-ssm-agent --classic
              sudo systemctl start snap.amazon-ssm-agent.amazon-ssm-agent.service
              sudo systemctl enable snap.amazon-ssm-agent.amazon-ssm-agent.service

              # AWS CLI 설치
              sudo apt-get update
              sudo apt-get install -y unzip
              curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
              unzip awscliv2.zip
              sudo ./aws/install

              # Docker 설치
              sudo apt-get install -y ca-certificates curl gnupg
              sudo install -m 0755 -d /etc/apt/keyrings
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
              sudo chmod a+r /etc/apt/keyrings/docker.gpg
              echo "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
              sudo apt-get update
              sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
              sudo usermod -aG docker ubuntu
              sudo systemctl start docker
              sudo systemctl enable docker

              # Docker Compose 설치
              sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
              sudo chmod +x /usr/local/bin/docker-compose

              # CloudWatch Agent 설치 및 설정
              sudo wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb
              sudo dpkg -i amazon-cloudwatch-agent.deb

              # CloudWatch Agent 설정
              sudo mkdir -p /opt/aws/amazon-cloudwatch-agent/etc/
              sudo bash -c 'cat > /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json' << 'EOT'
              {
                "agent": {
                  "metrics_collection_interval": 60,
                  "run_as_user": "root"
                },
                "metrics": {
                  "append_dimensions": {
                    "InstanceId": "$${aws:InstanceId}",
                    "InstanceType": "$${aws:InstanceType}",
                    "AutoScalingGroupName": "$${aws:AutoScalingGroupName}"
                  },
                  "metrics_collected": {
                    "mem": {
                      "measurement": [
                        "mem_used_percent"
                      ],
                      "metrics_collection_interval": 60
                    },
                    "swap": {
                      "measurement": [
                        "swap_used_percent"
                      ]
                    }
                  }
                }
              }
              EOT

              # CloudWatch Agent 시작
              sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json
              sudo systemctl start amazon-cloudwatch-agent
              sudo systemctl enable amazon-cloudwatch-agent
              EOF

  tags = {
    Name = "qrworld-app"
  }
}

# 탄력적 IP
resource "aws_eip" "app" {
  instance = aws_instance.app.id
  vpc      = true

  tags = {
    Name = "qrworld-app-eip"
  }
}

# 출력값
output "public_ip" {
  value = aws_eip.app.public_ip
}

output "ssh_command" {
  value = "ssh -i qrworld-key.pem ubuntu@${aws_eip.app.public_ip}"
}

output "instance_id" {
  value = aws_instance.app.id
  description = "EC2 인스턴스 ID"
}

# ECR 저장소 생성
resource "aws_ecr_repository" "app" {
  name = "backend-qrworld"
  force_delete = true  # 이 줄 추가

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "backend-qrworld"
  }
}

# ECR 저장소 정책 (선택사항)
resource "aws_ecr_repository_policy" "app_policy" {
  repository = aws_ecr_repository.app.name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AllowPushPull"
        Effect = "Allow"
        Principal = {
          AWS = "*"
        }
        Action = [
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:BatchCheckLayerAvailability",
          "ecr:PutImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload"
        ]
      }
    ]
  })
}

# 출력값 추가 (선택사항)
output "ecr_repository_url" {
  value = aws_ecr_repository.app.repository_url
}

# 키 페어 생성
resource "aws_key_pair" "qrworld" {
  key_name   = "qrworld-key"
  public_key = file("${path.module}/qrworld-key.pub")  # 로컬에 있는 public key 파일 경로
}

# RDS 보안 그룹
resource "aws_security_group" "rds" {
  name        = "qrworld-rds-sg"
  description = "Security group for RDS"
  vpc_id      = aws_vpc.main.id

  # 외부에서의 PostgreSQL 접속 허용
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    cidr_blocks     = ["0.0.0.0/0"]  # 모든 IP에서 접근 가능
  }

  tags = {
    Name = "qrworld-rds-sg"
  }
}

# RDS 서브넷 그룹
resource "aws_db_subnet_group" "rds" {
  name       = "qrworld-rds-subnet-group"
  subnet_ids = [aws_subnet.public_1.id, aws_subnet.public_2.id]

  tags = {
    Name = "qrworld-rds-subnet-group"
  }
}

# RDS 향상된 모니터링을 위한 IAM 역할
resource "aws_iam_role" "rds_enhanced_monitoring" {
  name = "qrworld-rds-enhanced-monitoring"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "monitoring.rds.amazonaws.com"
        }
      }
    ]
  })
}

# 향상된 모니터링 정책 연결
resource "aws_iam_role_policy_attachment" "rds_enhanced_monitoring" {
  role       = aws_iam_role.rds_enhanced_monitoring.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonRDSEnhancedMonitoringRole"
}

# RDS 인스턴스
resource "aws_db_instance" "qrworld" {
  identifier           = "qrworld-db"
  engine              = "postgres"
  engine_version      = "17.5"
  instance_class      = "db.t3.micro"
  allocated_storage   = 20
  storage_type        = "gp2"
  
  db_name             = "qrworld"
  username           = "qrworld_user"
  password           = var.db_password
  
  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.rds.name
  
  skip_final_snapshot    = true
  publicly_accessible    = true
  
  monitoring_interval = 60  # 60초마다 지표 수집
  monitoring_role_arn = aws_iam_role.rds_enhanced_monitoring.arn
  
  tags = {
    Name = "qrworld-db"
  }
}

# RDS 엔드포인트 출력
output "rds_endpoint" {
  value = aws_db_instance.qrworld.endpoint
}

# CloudWatch 대시보드 생성
resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = "qrworld-dashboard"

  dashboard_body = jsonencode({
    widgets = [
      {
        type   = "metric"
        x      = 0
        y      = 0
        width  = 12
        height = 6
        properties = {
          view    = "timeSeries"
          stacked = false
          metrics = [
            ["AWS/EC2", "CPUUtilization", "InstanceId", aws_instance.app.id]
          ]
          region = "ap-northeast-2"
          title  = "EC2 CPU 사용률 (%)"
          period = 300
          stat   = "Average"
          yAxis = {
            left = {
              min = 0
              max = 100
            }
          }
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 0
        width  = 12
        height = 6
        properties = {
          view    = "timeSeries"
          stacked = false
          metrics = [
            [
              "CWAgent",
              "mem_used_percent",
              "InstanceId", aws_instance.app.id,
              "InstanceType", aws_instance.app.instance_type
            ]
          ]
          region = "ap-northeast-2"
          title  = "EC2 메모리 사용률 (%)"
          period = 300
          stat   = "Average"
          yAxis = {
            left = {
              min = 0
              max = 100
            }
          }
        }
      },
      {
        type   = "metric"
        x      = 0
        y      = 6
        width  = 12
        height = 6
        properties = {
          view    = "timeSeries"
          stacked = false
          metrics = [
            ["AWS/RDS", "CPUUtilization", "DBInstanceIdentifier", aws_db_instance.qrworld.identifier]
          ]
          region = "ap-northeast-2"
          title  = "RDS CPU 사용률 (%)"
          period = 300
          stat   = "Average"
          yAxis = {
            left = {
              min = 0
              max = 100
            }
          }
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 6
        width  = 12
        height = 6
        properties = {
          view    = "timeSeries"
          stacked = false
          metrics = [
            ["AWS/RDS", "DatabaseConnections", "DBInstanceIdentifier", aws_db_instance.qrworld.identifier],
            ["AWS/RDS", "FreeableMemory", "DBInstanceIdentifier", aws_db_instance.qrworld.identifier],
            ["AWS/RDS", "FreeStorageSpace", "DBInstanceIdentifier", aws_db_instance.qrworld.identifier]
          ]
          region = "ap-northeast-2"
          title  = "RDS 상태"
          period = 300
          stat   = "Average"
        }
      }
    ]
  })
}

# EC2에 CloudWatch Agent 설치를 위한 IAM 정책 추가
resource "aws_iam_role_policy_attachment" "cloudwatch_agent" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
} 

# Amplify가 S3, CloudFront, CloudWatch Logs 등에 접근할 수 있도록 IAM Role 생성
resource "aws_iam_role" "amplify_service_role" {
  name = "qrworld-amplify-service-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "amplify.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })
}

# Amplify가 S3 Full Access
resource "aws_iam_role_policy_attachment" "amplify_s3" {
  role       = aws_iam_role.amplify_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}

# Amplify가 CloudFront Full Access
resource "aws_iam_role_policy_attachment" "amplify_cf" {
  role       = aws_iam_role.amplify_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudFrontFullAccess"
}

# Amplify가 CloudWatch Logs Full Access
resource "aws_iam_role_policy_attachment" "amplify_logs" {
  role       = aws_iam_role.amplify_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess"
}

# Amplify가 필요한 IAM 관리 기능(Branch 생성 등)을 위해 IAM Full Access
resource "aws_iam_role_policy_attachment" "amplify_iam" {
  role       = aws_iam_role.amplify_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/IAMFullAccess"
}

# Amplify 앱 생성
resource "aws_amplify_app" "front_app" {
  name                 = "qr-playground-frontend"
  repository           = "https://github.com/qr-playground/front"
  oauth_token          = var.github_oauth_token
  platform             = "WEB"
  iam_service_role_arn = aws_iam_role.amplify_service_role.arn

  default_branch = "develop"

  environment_variables = {
    VITE_API_BASE_URL = var.vite_api_base_url
  }

  build_spec = <<BUILD_SPEC
version: 1
frontend:
  phases:
    preBuild:
      commands:
        - nvm install 20
        - nvm use 20
        - npm ci --cache .npm --prefer-offline
    build:
      commands:
        - npm run build
  artifacts:
    baseDirectory: dist
    files:
      - '**/*'
  cache:
    paths:
      - .npm/**/*
BUILD_SPEC

  tags = {
    Project = "qr-world"
    Env     = "develop"
  }
}

# Amplify 브랜치 설정 (develop)
resource "aws_amplify_branch" "front_develop" {
  app_id            = aws_amplify_app.front_app.id
  branch_name       = "develop"
  framework         = "React"
  enable_auto_build = true
  stage             = "DEV"

  environment_variables = {
    VITE_API_BASE_URL = var.vite_api_base_url
  }

  tags = {
    Branch = "develop"
  }
}

# -------------------------------------------------------------
# 최종 출력 (중복 제거된 섹션)
# -------------------------------------------------------------

output "public_ip" {
  value = aws_eip.app.public_ip
}

output "ssh_command" {
  value = "ssh -i qrworld-key.pem ubuntu@${aws_eip.app.public_ip}"
}

output "instance_id" {
  value       = aws_instance.app.id
  description = "EC2 인스턴스 ID"
}

output "ecr_repository_url" {
  value = aws_ecr_repository.app.repository_url
}

output "rds_endpoint" {
  value = aws_db_instance.qrworld.endpoint
}