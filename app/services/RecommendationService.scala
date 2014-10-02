package services

import models.User

import scala.concurrent.Future
import nitroz.futures._
/**
 * Created by gsilin on 10/2/14.
 */
trait RecommendationServiceComponent {

  val recommendationService: RecommendationService
  trait RecommendationService {

    def recommendFavorites(username: String): Future[List[User]]
  }
}

trait RealRecommendationServiceComponent extends RecommendationServiceComponent {

  self: SoundCloudServiceComponent =>

  lazy val recommendationService: RecommendationService = new RecommendationService {
    def recommendFavorites(username: String): Future[List[User]] = {
      // step 1 get following
      // step 2 get their favorites
      // step 3 get unique artists
      // step 3b filter on ones I'm following
      // step 4 order by frequency
      // step 5 return top 10
      for {
        // step 1 get following
        myFollowings <- soundCloudService.getFollowings(username)
      // only consider those with likes
        myFollowingsWithFavorites = myFollowings filter (_.public_favorites_count.getOrElse(0) > 0)
      _ = println(s"Got ${myFollowingsWithFavorites.size} followings who have favorites")
      // step 1b get favorites for me
        myFavorites <- soundCloudService.getFavoriteTracks(username)
        myArtists = (myFavorites map (_.user.username)).distinct
        _ = println(s"Got ${myArtists.size} artists who have favorites")

      } yield myFollowingsWithFavorites
    }

  }
}
