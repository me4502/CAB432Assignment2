resource "aws_autoscaling_group" "ecs-autoscaling-group" {
  name = "ecs-autoscaling-group"
  max_size = "${var.max_instances}"
  min_size = "${var.min_instances}"
  vpc_zone_identifier = [
    "${var.subnet_a}",
    "${var.subnet_b}",
    "${var.subnet_c}"
  ]
  target_group_arns = [
    "${aws_alb_target_group.ecs-target-group.arn}"
  ]
  launch_configuration = "${aws_launch_configuration.ecs-launch-configuration.name}"
  health_check_type = "ELB"
  default_cooldown = 60
  health_check_grace_period = 120
}

resource "aws_autoscaling_policy" "scaling_policy" {
  autoscaling_group_name = "${aws_autoscaling_group.ecs-autoscaling-group.name}"
  name = "scaling_policy"
  adjustment_type = "ChangeInCapacity"
  policy_type = "TargetTrackingScaling"

  target_tracking_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ASGAverageCPUUtilization"
    }
    target_value = 40.0
  }
}