package controllers

import models.Track
import nitroz.{TypesafeController, cake}
import nitroz.futures._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
 * Created by gsilin on 10/2/14.
 */
object SoundCloud extends TypesafeController {

  def favorites(username: String) = asyncAction() { implicit request =>
    cake.soundCloudService.getFavoriteTracks(username) 
  }
}
