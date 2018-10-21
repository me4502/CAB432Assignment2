resource "aws_alb" "ecs-load-balancer" {
  name = "ecs-load-balancer"
  security_groups = [
    "${var.security_group}"
  ]
  subnets = [
    "subnet-00aaa367",
    "subnet-811c25c8",
    "subnet-bf18dce6"
  ]

  tags {
    Name = "ecs-load-balancer"
  }
}

resource "aws_alb_target_group" "ecs-target-group" {
  name = "ecs-target-group"
  port = "80"
  protocol = "HTTP"
  vpc_id = "${var.vpc_name}"

  health_check {
    healthy_threshold = "5"
    unhealthy_threshold = "2"
    interval = "30"
    matcher = "200"
    path = "/"
    port = "traffic-port"
    protocol = "HTTP"
    timeout = "5"
  }

  tags {
    Name = "ecs-target-group"
  }

  depends_on = [
    "aws_alb.ecs-load-balancer"
  ]
}

resource "aws_alb_listener" "alb-listener" {
  load_balancer_arn = "${aws_alb.ecs-load-balancer.arn}"
  port = "80"
  protocol = "HTTP"
  depends_on = [
    "aws_alb.ecs-load-balancer"
  ]

  default_action {
    target_group_arn = "${aws_alb_target_group.ecs-target-group.arn}"
    type = "forward"
  }
}
