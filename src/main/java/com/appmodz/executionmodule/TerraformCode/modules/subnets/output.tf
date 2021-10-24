output "subnet_id" {
  value = aws_subnet.public_subnets.*.id
  description = "The Subnet Id of the VPC created"
}