import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import javax.inject.Singleton

import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits._

@Singleton
class ErrorHandler extends HttpErrorHandler {


  case class Error(message: String, code: Int, field: String)

  case class Message(message: String, errors: Error)

  case class MessageServer(message: String)

  implicit val errorsClientWrites: Writes[Error] = (
    (JsPath \ "message").write[String] and
      (JsPath \ "code").write[Int] and
      (JsPath \ "field").write[String]
    ) (unlift(Error.unapply))

  implicit val msgClientWrites: Writes[Message] = (
    (JsPath \ "message").write[String] and
      (JsPath \ "errors").write[Error]
    ) (unlift(Message.unapply))

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    val error: Error = Error("Oops! The format is not correct", 35, message)
    val msg: Message = Message("Validation errors in your request", error)
    val jsonError: JsValue = Json.toJson(msg)
    Future.successful(
      BadRequest(jsonError)
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    try {

      Future.successful(
        InternalServerError(Json.obj("message" -> "Something is broken",
          "errors" -> Json.obj("message" -> exception.getMessage, "cause" -> exception.getCause().toString)))
      )
    } catch {
      case _: NullPointerException => Future(InternalServerError(Json.obj(("message" -> "Something is broken"))))
    }

  }


}