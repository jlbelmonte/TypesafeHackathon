package controllers

import nitroz.{cake, TypesafeController}

/**
 * Created by gsilin on 10/2/14.
 */
object Recommendations extends TypesafeController {


  def favorites(username: String) = asyncAction() { implicit request =>
    cake.recommendationService.recommendFavorites(username)
  }
}
