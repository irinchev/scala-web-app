package my.test.cache

import org.redisson.config.Config

object Redis {
  val config = new Config
  config.useClusterServers()
    .addNodeAddress("redis://127.0.0.1:20000")
    .addNodeAddress("redis://127.0.0.1:20002")
    .addNodeAddress("redis://127.0.0.1:20004")

  def get() = {

  }

}
