output "vpc_id" {
  value = aws_vpc.vpc.id
  description = "The VPC Id of the VPC"
}
output "vpc_arn" {
  value = aws_vpc.vpc.arn
  description = "The VPC Amazon Resource Name of the VPC"
}