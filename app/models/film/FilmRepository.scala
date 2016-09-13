package models.film

import javax.inject.Inject

import util.CouchbaseBuckets

import scala.concurrent.Future

class FilmRepository @Inject()(wrapper: CouchbaseBuckets) {

  implicit val ec = wrapper.defaultExecutionContext

  // Upsert a film in db using default writer from companion object
  def create(film: Film): Future[Film] =
    wrapper.defaultBucket
      .set(FilmDbKey(film.isbn), film)
      .flatMap { result =>
        if (result.isSuccess) {
          Future.successful(film)
        } else {
          Future.failed(new IllegalArgumentException(s"Impossible to update film. Cause: ${result.getMessage}"))
        }
      }

  // Get a film from the db using default reader from companion object
  def get(isbn: String): Future[Option[Film]] =
    wrapper.defaultBucket
      .get[Film](FilmDbKey(isbn))

}
