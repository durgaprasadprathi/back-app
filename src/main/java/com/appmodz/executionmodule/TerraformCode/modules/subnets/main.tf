resource "aws_subnet" "public_subnets" {
  count      = length(split(",", var.public_subnets))
  vpc_id     = var.vpc_id
  map_public_ip_on_launch = true
  cidr_block  = element(split(",", var.public_subnets), count.index)
  availability_zone = element(split(",", var.availability_zones), count.index)
}
