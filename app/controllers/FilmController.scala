package controllers

import javax.inject._

import models.film.{Film, FilmRepository}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class FilmController @Inject()(filmRepository: FilmRepository)(implicit exec: ExecutionContext) extends Controller {

  def createFilm = Action.async(parse.json) { implicit request =>
    request.body.validate[Film].map { film =>
      Logger.debug(s"Creating film using isbn: ${film.isbn}")
      filmRepository.create(film)
        .map { f =>
          Logger.info("Successfully created a film")
          Created(Json.toJson(f))
        }
        .recover {
          case e: Throwable =>
            Logger.error("Error creating film", e)
            InternalServerError("Error creating film")
        }
    }.recoverTotal { e =>
      Logger.warn(s"Unable to create a film: ${JsError.toJson(e)}")
      Future.successful(BadRequest(Json.obj("messages" -> "unable to create film")))
    }
  }

  def findFilmBy(isbn: String) = Action.async { implicit request =>
    filmRepository.get(isbn).map { maybeFilm =>
      maybeFilm
        .map(f => Ok(Json.toJson(f)))
        .getOrElse(NotFound(isbn))
    }.recover {
      case e: Throwable =>
        Logger.error(s"Error finding film using isbn: $isbn", e)
        InternalServerError("Error finding films")
    }
  }

}
