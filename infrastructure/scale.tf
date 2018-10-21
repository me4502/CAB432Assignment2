resource "aws_autoscaling_group" "ecs-autoscaling-group" {
  name                        = "ecs-autoscaling-group"
  max_size                    = 4
  min_size                    = 1
  desired_capacity            = 2
  vpc_zone_identifier = [
    "subnet-00aaa367",
    "subnet-811c25c8",
    "subnet-bf18dce6"
  ]
  target_group_arns = [
    "${aws_alb_target_group.ecs-target-group.arn}"
  ]
  launch_configuration        = "${aws_launch_configuration.ecs-launch-configuration.name}"
  health_check_type           = "ELB"
}
