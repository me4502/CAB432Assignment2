resource "aws_elasticache_cluster" "redis-cache" {
  cluster_id = "redis-cache"
  engine = "redis"
  node_type = "cache.m4.large"
  num_cache_nodes = 1
  parameter_group_name = "default.redis4.0"
  port = 6379
}
