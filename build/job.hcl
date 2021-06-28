job "infra-redis-1" {

  datacenters = ["daff"]
  type = "service"

  constraint {
    attribute = "${node.class}"
    value = "MAIN"
  }

  group "infra-redis-group-1" {
    count = 1
    update {
      max_parallel = 0
    }
    network {
      port "redis_port_m" {
        static = 20000
      }
      port "redis_port_r" {
        static = 20001
      }
    }
    restart {
      attempts = 2
      interval = "5m"
      delay = "15s"
      mode = "fail"
    }

    task "infra-redis-master-1" {
      driver = "raw_exec"
      artifact {
        source = "http://buddy:8081/repository/buddy-raw/redis/redis-server"
      }
      template {
        data = <<EOH
port 20000
protected-mode no
pidfile redis.pid
dbfilename dump.rdb
dir /opt/redis-tmp/20000
appendonly yes
appendfsync everysec
appendfilename "datalog.aof"
cluster-enabled yes
cluster-node-timeout 15000
EOH
        destination = "local/redis.conf"
        change_mode = "noop"
      }
      config {
        command = "${NOMAD_TASK_DIR}/redis-server"
        args    = ["${NOMAD_TASK_DIR}/redis.conf"]
      }
      resources {
        cpu = 100
        memory = 256
      }
      service {
        name = "redis-node-m-1"
        port = "redis_port_m"
        check {
          name = "redis-node-m-1 port alive"
          type = "tcp"
          interval = "10s"
          timeout = "2s"
        }
      }
    }

    task "infra-redis-replica-1" {
      driver = "raw_exec"
      artifact {
        source = "http://buddy:8081/repository/buddy-raw/redis/redis-server"
      }
      lifecycle {
        hook    = "prestart"
        sidecar = true
      }
      template {
        data = <<EOH
port 20001
protected-mode no
pidfile redis.pid
rdbcompression yes
dbfilename dump.rdb
dir /opt/redis-tmp/20000/replica
appendonly yes
appendfsync everysec
appendfilename "datalog.aof"
cluster-enabled yes
cluster-node-timeout 15000
EOH
        destination = "local/redis-replica.conf"
        change_mode = "noop"
      }
      config {
        command = "${NOMAD_TASK_DIR}/redis-server"
        args    = ["${NOMAD_TASK_DIR}/redis-replica.conf"]
      }
      resources {
        cpu = 100
        memory = 256
      }
      service {
        name = "redis-node-r-1"
        port = "redis_port_r"
        check {
          name = "redis-node-r-1 port alive"
          type = "tcp"
          interval = "10s"
          timeout = "2s"
        }
      }
    }

  }
}
