variable "db_password" {
  description = "RDS root user password"
  type        = string
  sensitive   = true
} 

variable "github_oauth_token" {
  type        = string
  description = "GitHub OAuth token for Amplify to access the repository"
  sensitive   = true
}

variable "vite_api_base_url" {
  type        = string
  description = "VITE_API_BASE_URL for Vite-built frontend"
}