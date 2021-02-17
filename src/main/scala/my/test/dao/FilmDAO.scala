package my.test.dao

import com.typesafe.scalalogging._
import my.test.Film
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

object FilmDAO extends LazyLogging {
  def findMovies(): List[Film] =
    DB readOnly { implicit session =>
      sql"SELECT * FROM film limit 100"
        .map(result =>
          Film(
            result.string("title"),
            result.string("description"),
            "EN",
            result.int("length"),
            result.int("release_year")
          )
        )
        .list
        .apply()
    }
}
