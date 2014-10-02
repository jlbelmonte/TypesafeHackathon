package controllers

import controllers.Application._
import models.Track
import nitroz.{TypesafeController, cake}
import nitroz.futures._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{Await, Future}

/**
 * Created by gsilin on 10/2/14.
 */
object SoundCloud extends TypesafeController {

  def favorites(username: String) = asyncAction() { implicit request =>
    cake.soundCloudService.getFavoriteTracks(username) 
  }

  def following(username: String) = asyncAction() { implicit request =>
    cake.soundCloudService.getFollowings(username)
  }

  def followingFollowing(username: String) = asyncAction() { implicit request =>
    cake.soundCloudService.getFollowingsFollowings(username)
  }
  import scala.concurrent.duration._
  def followingFollowing2(username: String) = Action { implicit request =>
    val foo = Await.result(cake.soundCloudService.getFollowingsFollowings(username),(60 seconds))
    Ok(views.html.main("Recomendations", foo))
  }
}
