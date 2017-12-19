package controllers

import play.api.mvc._
import services.connectors._
import services.models.{Tick, ViewModels}

import scala.concurrent.ExecutionContext.Implicits.global

class DRMTodayController extends Controller {

  def drm: Action[AnyContent] = Action.async { request =>
    DRMProducer.produce(request.body.asJson.get.toString()).map { r =>
      r match {
        case false => InternalServerError(ViewModels.MSG_ERROR_JSON)
        case true => Ok(ViewModels.MSG_SUCCESS_JSON)
      }
    }
  }

  def postGenTick = Action.async { request =>
    // Send it to Kafka
    DRMProducer.produce(Tick(System.currentTimeMillis())).map { r =>
      r match {
        case false => InternalServerError(ViewModels.MSG_ERROR_JSON)
        case true => Ok(ViewModels.MSG_SUCCESS_JSON)
      }
    }
  }

}