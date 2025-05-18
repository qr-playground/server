#!/usr/bin/env bash
# build-and-run.sh
# chmod +x build-and-run.sh 
set -e                                # 오류 발생 시 즉시 종료

IMAGE_NAME=qrworld                    # 이미지 태그
CONTAINER_NAME=qrworld-app            # 컨테이너 이름
HOST_IP=host.docker.internal          # 호스트 IP
HOST_PORT=8080                        # 호스트에서 열 포트
SPRING_PROFILE=${SPRING_PROFILE:-local}  # 기본 프로필(local)
PINPOINT_VERSION=3.0.2
PINPOINT_HOME=/opt/pinpoint-agent-${PINPOINT_VERSION}


echo "▶ Docker build…"
docker build -t $IMAGE_NAME .

echo "▶ (있다면) 이전 컨테이너 정리…"
docker rm -f $CONTAINER_NAME 2>/dev/null || true

echo "▶ Docker run…"
docker run -d --name $CONTAINER_NAME \
  -p $HOST_PORT:8080 \
  -e SPRING_PROFILES_ACTIVE=$SPRING_PROFILE \
  -e APP_HOST=$HOST_IP \
  -e COLLECTOR_IP=13.124.159.201 \
  -e PINPOINT_AGENT_ID=qrworld-agent \
  -e PINPOINT_APPLICATION_NAME=qrworld \
  -e JAVA_TOOL_OPTIONS="\
-javaagent:${PINPOINT_HOME}/pinpoint-bootstrap.jar \
-Dpinpoint.agentId=qrworld-agent \
-Dpinpoint.applicationName=qrworld \
-Dprofiler.transport.grpc.collector.ip=13.124.159.201 \
-Dprofiler.transport.grpc.collector.port=9991 \
-Dprofiler.transport.grpc.stat.port=9992 \
-Dprofiler.transport.grpc.span.port=9993 \
-Dprofiler.sampling.enable=true \ 
-Dprofiler.sampling.counting.sampling-rate=1" \
  $IMAGE_NAME

echo "✅ 컨테이너가 http://$HOST_IP:$HOST_PORT 에서 실행 중입니다."