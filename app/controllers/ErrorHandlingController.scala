package controllers

import play.api.mvc._
import services.models.{Tick, ViewModels}
import services.connectors.EHProducer

import scala.concurrent.ExecutionContext.Implicits.global

class ErrorHandlingController extends Controller {

  def eh: Action[AnyContent] = Action.async { request =>
    EHProducer.produce(request.body.asJson.get.toString()).map { r =>
      r match {
        case false => InternalServerError(ViewModels.MSG_ERROR_JSON)
        case true => Ok(ViewModels.MSG_SUCCESS_JSON)
      }
    }
  }

  def mobile: Action[AnyContent] = Action.async { request =>
    EHProducer.produce(request.body.asJson.get.toString()).map { r =>
      r match {
        case false => InternalServerError(ViewModels.MSG_ERROR_JSON)
        case true => Ok(ViewModels.MSG_SUCCESS_JSON)
      }
    }
  }

  def web: Action[AnyContent] = Action.async { request =>
    EHProducer.produce(request.body.asJson.get.toString()).map { r =>
      r match {
        case false => InternalServerError(ViewModels.MSG_ERROR_JSON)
        case true => Ok(ViewModels.MSG_SUCCESS_JSON)
      }
    }
  }

  def postGenTick = Action.async { request =>
    // Send it to Kafka
    EHProducer.produce(Tick(System.currentTimeMillis())).map { r =>
      r match {
        case false => InternalServerError(ViewModels.MSG_ERROR_JSON)
        case true => Ok(ViewModels.MSG_SUCCESS_JSON)
      }
    }
  }

}