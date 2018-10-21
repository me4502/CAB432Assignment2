data "aws_ecs_task_definition" "sentiment" {
  task_definition = "${aws_ecs_task_definition.sentiment.family}"
}

resource "aws_ecs_task_definition" "sentiment" {
  family = "sentiment_task"
  container_definitions = <<DEFINITION
[
  {
    "name": "sentiment",
    "image": "me4502/twitter-sentiment:${var.docker_tag}",
    "essential": true,
    "portMappings": [
      {
        "containerPort": 5078,
        "hostPort": 80
      }
    ],
    "memory": 500,
    "cpu": 10
  }
]
DEFINITION
}
