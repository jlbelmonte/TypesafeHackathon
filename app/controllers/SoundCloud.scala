package controllers

import models.Track
import nitroz.cake
import nitroz.futures._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
 * Created by gsilin on 10/2/14.
 */
object SoundCloud extends Controller {

  def favorites(username: String) = Action.async { implicit request =>
    cake.soundCloudService.getFavoriteTracks(username) map {
      case tracks => Ok(Json.toJson[List[Track]](tracks))
    }

  }
}
