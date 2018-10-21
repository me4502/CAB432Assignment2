resource "aws_ecs_service" "main-ecs-service" {
  name = "main-ecs-service"
  iam_role = "${aws_iam_role.ecs-service-role.name}"
  cluster = "${aws_ecs_cluster.main-ecs-cluster.id}"
  task_definition = "${aws_ecs_task_definition.sentiment.family}:${max("${aws_ecs_task_definition.sentiment.revision}", "${data.aws_ecs_task_definition.sentiment.revision}")}"
  desired_count = "${var.max_instances}"

  load_balancer {
    target_group_arn = "${aws_alb_target_group.ecs-target-group.arn}"
    container_port = 5078
    container_name = "sentiment"
  }

  depends_on = [
    "aws_alb_target_group.ecs-target-group",
    "aws_ecs_task_definition.sentiment"
  ]
}
