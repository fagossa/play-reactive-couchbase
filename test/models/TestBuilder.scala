package models

import play.api.Application
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.{FakeHeaders, FakeRequest, Helpers}
import play.mvc.Call

import scala.concurrent.Future

object TestBuilder {

  object anUser {

    import play.api.test.Helpers._
    def GET(call: Call)(implicit app: Application): Option[Future[Result]] =
      route(app, fakeUnauthenticatedRequest(Helpers.GET, call.url(), FakeHeaders(), AnyContentAsEmpty))

    def POST[A](call: Call)(body: A)(implicit w: play.api.http.Writeable[A], app: Application): Option[Future[Result]] =
      route(app, fakeUnauthenticatedRequest(Helpers.POST, call.url(), jsonHeaders, body))

    private def fakeUnauthenticatedRequest[A](method: String, uri: String, headers: FakeHeaders, body: A): FakeRequest[A] = FakeRequest(
      method,
      uri,
      withAjaxHeader(headers),
      body
    )

    private def jsonHeaders = FakeHeaders(
      Seq("Content-Type" -> "application/json")
    )

    private def withAjaxHeader(headers: FakeHeaders): FakeHeaders = headers.copy(data = headers.data :+ ("X-Requested-With" -> "XMLHttpRequest"))
  }

}
