package models.film

import play.api.libs.json.Json

case class Film(isbn: String, name: String, release: Int)

object Film {
  implicit val defaultReads = Json.reads[Film]
  implicit val defaultWrites = Json.writes[Film]
}
