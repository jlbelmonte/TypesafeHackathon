package controllers

import nitroz.{cake, TypesafeController}
import play.api.mvc.Controller
import play.api.mvc.Results._

/**
 * Created by gsilin on 10/2/14.
 */
object Favorites extends TypesafeController {

  def favoritesSummary(username: String) = asyncAction() { implicit request =>

  cake.soundCloudService.getFavoritesSummary(username)
  }

}
