resource "aws_elasticache_cluster" "redis-cache" {
  cluster_id = "redis-cache"
  engine = "redis"
  node_type = "cache.t2.medium"
  num_cache_nodes = 1
  parameter_group_name = "default.redis4.0"
  port = 6379
}
