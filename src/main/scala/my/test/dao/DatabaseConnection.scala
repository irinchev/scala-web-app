package my.test.dao

import java.sql.Connection
import com.typesafe.config.Config
import scalikejdbc.{ConnectionPool, GlobalSettings, LoggingSQLAndTimeSettings}

object DatabaseConnection {
  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(enabled = false, singleLineMode = false)
  GlobalSettings.loggingConnections = false

  def connection(config: Config): Connection = {
    val url = config.getString("url")
    val driver = config.getString("driver")
    val username = config.getString("username")
    val password = config.getString("password")
    Class.forName(driver)
    ConnectionPool.singleton(url, username, password)
    ConnectionPool.borrow()
  }
}
