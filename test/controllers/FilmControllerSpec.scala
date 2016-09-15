package controllers

import controllers.FilmControllerSpec.TestableFilmBucket
import models.TestBuilder._
import models.film.{Film, FilmDbKey, Isbn}
import net.spy.memcached.ops.OperationStatus
import org.mockito.Mockito._
import org.reactivecouchbase.CouchbaseBucket
import org.reactivecouchbase.client.OpResult
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import util.{CouchbaseBuckets, OneAppPerTestWithOverrides}

import scala.concurrent.Future

class FilmControllerSpec extends PlaySpec with OneAppPerTestWithOverrides
  with ScalaFutures with MockitoSugar {

  implicit lazy val ec = app.materializer.executionContext

  import controllers.FilmControllerSpec.TestableFilmBucket._

  "a Film controller" should {

    "create films" in {
      val response = anUser.POST(routes.FilmController.createFilm())(Json.parse(
        """
          |{"isbn": "tt1139797", "name": "Låt den rätte komma", "release": 2008}
        """.stripMargin)).value
      status(response) must be(CREATED)
    }

    "get existing films" in {
      // Given
      val anExistingIsbn = Isbn("12345")
      // When
      val response = anUser.GET(routes.FilmController.findFilmBy(anExistingIsbn)).value
      // Then
      status(response) must be(OK)
      contentAsJson(response).as[Film] mustBe existingFilms.head
    }

    "detect non existing films" in {
      // Given
      val anUnknownIsbn = Isbn("xxxx")
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

  private val successCbResponse = new OpResult(ok = true, None, None, 0, None)

  // This singleton instance gets injected into your FilmRepository
  object TestableFilmBucket extends CouchbaseBuckets(MockedApplicationLifecycle) with MockitoSugar {
    override lazy val defaultBucket = mock[CouchbaseBucket]

    implicit val ec = defaultExecutionContext

    // by default all films are rejected
    when(defaultBucket.get[Film](anyString())(anyObject(), anyObject()))
      .thenReturn(Future.successful(None))

    // by default success response after inserting
    when(defaultBucket.set[Film](anyString(), anyObject(), anyObject(), anyObject(), anyObject())(anyObject(), anyObject()))
        .thenReturn(Future.successful(successCbResponse))

    // we have a list of existing films
    val existingFilms = List(
      Film(Isbn("12345"), "Jurassic Park", 1993)
    )

    // prepares the mocked object to handle data
    existingFilms.foreach { film =>
      when(defaultBucket.get[Film](FilmDbKey(film.isbn)))
        .thenReturn(Future.successful(Some(film)))
    }

  }

}
