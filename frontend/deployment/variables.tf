variable "image" {
  type = string
}

variable "application" {
  type = string
  default = "skoda-hackathon-2022-frontend"
}

variable "environment" {
  type = string
  default = "skoda-hackathon-2022-default"
}

variable "project" {
  type = string
  default = "skoda-hackathon-2022"
}

variable "port" {
  type = number
  default = 80
}

variable "container_registry" {
  type = string
  default = "skodahackathon2022"
}