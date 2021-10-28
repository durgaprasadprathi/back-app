output "vpc_id" {
  value = module.aws_vpc.vpc_id
  description = "The VPC Id of the VPC"
}
output "vpc_arn" {
  value = module.aws_vpc.vpc_arn
  description = "The VPC Amazon Resource Name of the VPC"

}
