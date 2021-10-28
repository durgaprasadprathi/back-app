terraform {
  required_providers {
    aws ={
      version = "~>3.53"
      source = "hashicorp/aws"
    }
  }
}


provider "aws" {
  region = var.aws_region
  access_key = var.access_key
  secret_key = var.secret_key
}


data "aws_availability_zones" "azs" {
    state = "available"
}
