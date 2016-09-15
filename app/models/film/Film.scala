package models.film

import play.api.libs.json.{JsError, JsSuccess, Writes, _}
import play.api.mvc.PathBindable

object FilmDbKey {
  def apply(id: Isbn) = s"film::${id.value}"
}

case class Isbn(value: String) extends AnyVal

object Isbn {

  implicit val isbnFromPath: PathBindable[Isbn] = new PathBindable[Isbn] {
    override def bind(key: String, value: String): Either[String, Isbn] =
      Right(Isbn(value))

    override def unbind(key: String, isbn: Isbn): String =
      isbn.value
  }

  implicit val reads: Reads[Isbn] = Reads {
    case str: JsString => JsSuccess(new Isbn(str.value))
    case default => JsError("Not an Isbn")
  }

  implicit val writes: Writes[Isbn] = Writes { (id: Isbn) => JsString(id.value)}
}

case class Film(isbn: Isbn, name: String, release: Int)

object Film {

  implicit val defaultFormat = Json.format[Film]
}
