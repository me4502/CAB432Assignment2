provider "aws" {
  region = "ap-southeast-1"
}

data "aws_ami" "application_ami" {
  most_recent      = true

  filter {
    name   = "name"
    values = ["amzn-ami-vpc-nat*"]
  }

  name_regex = "^myami-\\d{3}"
  owners     = ["self"]
}

module "sentiment_asg" {
  source = "terraform-aws-modules/autoscaling/aws"

  name = "sentiment-app"

  # Launch configuration
  #
  # launch_configuration = "my-existing-launch-configuration" # Use the existing launch configuration
  # create_lc = false # disables creation of launch configuration
  lc_name = "example-lc"

  image_id                     = "${data.aws_ami.application_ami.id}"
  instance_type                = "t3.micro"
  associate_public_ip_address  = true
  recreate_asg_when_lc_changes = true

  # Auto scaling group
  asg_name                  = "example-asg"
  vpc_zone_identifier       =  ["vpc-f32e2a97"]
  health_check_type         = "EC2"
  min_size                  = 0
  max_size                  = 4
  desired_capacity          = 0
  wait_for_capacity_timeout = 0
}

