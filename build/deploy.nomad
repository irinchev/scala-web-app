job "web-app" {
  datacenters = ["daff"]
  type = "service"
  group "web-app-group" {
    count = 2
    network {
      port "appPort" {}
      port "admPort" {}
      port "rcpPort" {}
    }
    task "web-app-task" {
      driver = "raw_exec"
      config {
        command = "java"
        args    = ["-Xms2G", "-Xmx4G", "-Dconfig.resource=dev.conf", "-jar", "${NOMAD_TASK_DIR}/@@JAR@@"]
      }
      env {
        APP_VERSION   = "@@VER@@"
        APP_IP        = "${NOMAD_IP_appPort}"
        APP_MGMT_PORT = "${NOMAD_PORT_admPort}"
        APP_HTTP_PORT = "${NOMAD_PORT_appPort}"
        APP_RACP_PORT = "${NOMAD_PORT_rcpPort}"
        JAVA_OPTS     = "-Dconfig.resource=dev.conf"
      }
      artifact {
        source = "@@URL@@"
      }
      service {
        name = "web-app-service"
        tags = ["urlprefix-/feeder strip=/feeder", "akka-management-port:${NOMAD_PORT_admPort}", "actor-system-name:web-app-system"]
        port = "appPort"
        check {
          name     = "alive"
          type     = "tcp"
          port     = "appPort"
          interval = "10s"
          timeout  = "2s"
        }
      }
      resources {
        cpu    = 500 # 500 MHz
        memory = 256 # 256MB
      }
    }
  }
}