variable "aws_region" {
  type = string

}

variable "access_key" {
  type = string
}

variable "secret_key" {
  type = string
}

#The Workspace Name. Defaults to a Random String if not provided
variable "workspace_name" {
  type = string
  description = "The Workspace Name associated with this stack"
}


