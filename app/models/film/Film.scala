package models.film

import play.api.libs.json.Json

object FilmDbKey {
  def apply(id: String) = s"film::$id"
}

case class Film(isbn: String, name: String, release: Int)

object Film {
  implicit val defaultFormat = Json.format[Film]
}
