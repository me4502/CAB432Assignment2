provider "aws" {
  region = "${var.region}"
}

variable "region" {
  description = "AWS Region"
}

variable "ecs_key_pair_name" {
  description = "EC2 instance key pair name"
}

variable "vpc_name" {
  description = "ID of the VPC"
}

variable "security_group" {
  description = "The security group"
}

variable "docker_tag" {
  description = "Docker tag"
}

variable "max_instances" {
  default = 16
}

variable "min_instances" {
  default = 2
}