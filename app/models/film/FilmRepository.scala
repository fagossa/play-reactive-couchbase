package models.film

import javax.inject.Inject

import play.api.libs.json.Json
import util.CouchbaseBuckets

import scala.concurrent.Future

class FilmRepository @Inject()(wrapper: CouchbaseBuckets) {

  implicit val ec = wrapper.defaultExecutionContext

  def create(film: Film): Future[Film] =
    wrapper.defaultBucket
      .set(s"film::${film.isbn}", film)
      .flatMap { result =>
        if (result.isSuccess) {
          Future.successful(film)
        } else {
          Future.failed(new IllegalArgumentException(s"Impossible to update film. Cause: ${result.getMessage}"))
        }
      }

  def get(isbn: String): Future[Option[Film]] =
    wrapper.defaultBucket
      .get[Film](s"film::$isbn")

}
