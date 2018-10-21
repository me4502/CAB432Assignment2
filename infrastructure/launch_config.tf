resource "aws_launch_configuration" "ecs-launch-configuration" {
  name                        = "ecs-launch-configuration"
  image_id                    = "ami-0d4d4a42a45fb8e4a"
  instance_type               = "t2.medium"
  iam_instance_profile        = "${aws_iam_instance_profile.ecs-instance-profile.id}"

  root_block_device {
    volume_type = "standard"
    volume_size = 100
    delete_on_termination = true
  }

  lifecycle {
    create_before_destroy = true
  }

  security_groups             = ["${var.security_group}"]
  associate_public_ip_address = "true"
  key_name                    = "${var.ecs_key_pair_name}"
  user_data                   = <<EOF
                                  #!/bin/bash
                                  echo ECS_CLUSTER=main-ecs-cluster >> /etc/ecs/ecs.config
                                  EOF
}
