package controllers

import controllers.FilmControllerSpec.TestableFilmBucket
import models.TestBuilder._
import models.film.{Film, FilmDbKey}
import org.mockito.Mockito._
import org.reactivecouchbase.CouchbaseBucket
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers._
import util.{CouchbaseBuckets, OneAppPerTestWithOverrides}

import scala.concurrent.Future

class FilmControllerSpec extends PlaySpec with OneAppPerTestWithOverrides
  with ScalaFutures with MockitoSugar {

  implicit lazy val ec = app.materializer.executionContext

  import controllers.FilmControllerSpec.TestableFilmBucket._

  "a Film controller" should {

    "get existing films" in {
      // Given
      val anExistingIsbn = "12345"
      // When
      val response = anUser.GET(routes.FilmController.findFilmBy(anExistingIsbn)).value
      // Then
      status(response) must be(OK)
      contentAsJson(response).as[Film] mustBe existingFilms.head
    }

    "detect non existing films" in {
      // Given
      val anUnknownIsbn = "xxxx"
      // When
      val response = anUser.GET(routes.FilmController.findFilmBy(anUnknownIsbn)).value
      // Then
      status(response) must be(NOT_FOUND)
    }

  }

  // This is where you replace the real bucketWrapper with the mocked one
  import play.api.inject.bind

  override def overrideModules = Seq(
    bind[CouchbaseBuckets].to[TestableFilmBucket.type]
  )

}

object FilmControllerSpec {

  import org.mockito.Matchers.{anyObject, anyString}
  import play.api.inject.ApplicationLifecycle

  object MockedApplicationLifecycle extends ApplicationLifecycle {
    override def addStopHook(hook: () => Future[_]): Unit = {}
  }

  // This singleton instance gets injected into your FilmRepository
  object TestableFilmBucket extends CouchbaseBuckets(MockedApplicationLifecycle) with MockitoSugar {
    override lazy val defaultBucket = mock[CouchbaseBucket]

    implicit val ec = defaultExecutionContext

    // by default all films are rejected
    when(defaultBucket.get[Film](anyString())(anyObject(), anyObject()))
      .thenReturn(Future.successful(None))

    // we have a list of existing films
    val existingFilms = List(
      Film("12345", "Jurassick Park", 1993)
    )

    // prepares the mocked object to handle data
    existingFilms.foreach { film =>
      when(defaultBucket.get[Film](FilmDbKey(film.isbn)))
        .thenReturn(Future.successful(Some(film)))
    }

  }

}
