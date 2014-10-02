package services

import models.{FavoriteArtist, Track, User}

import scala.concurrent.Future
import nitroz.futures._
/**
 * Created by gsilin on 10/2/14.
 */
trait RecommendationServiceComponent {

  val recommendationService: RecommendationService
  trait RecommendationService {

    def recommendFavorites(username: String, top: Int): Future[List[FavoriteArtist]]
  }
}

trait RealRecommendationServiceComponent extends RecommendationServiceComponent {

  self: SoundCloudServiceComponent =>

  lazy val recommendationService: RecommendationService = new RecommendationService {
    def recommendFavorites(username: String, top: Int): Future[List[FavoriteArtist]] = {
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
        myFollowingsToCheck = myFollowingsWithFavorites.sortBy(_.public_favorites_count).reverse.take(top)
        // step 1b get favorites for me
        myFavorites <- soundCloudService.getFavoriteTracks(username)
        myArtists = (myFavorites map (_.user.username)).distinct
        _ = println(s"Got ${myArtists.size} artists who have favorites")
        theirFavorites <- soundCloudService.getFavoriteTracksBatch(myFollowingsToCheck map (_.id.toString))
        allTracksFiltered = theirFavorites.values.toList.flatten.filterNot(track => myArtists.contains(track.user.username))
        recommended = faveArtists(allTracksFiltered)

      } yield recommended
    }

    //todo ther is a copy in soundcloudservcie
    private def faveArtists(tracks: List[Track]) = {
      val byArtist = tracks.groupBy(_.user.username)
      val byTracks = byArtist map {
        case (artist, track :: tracksTail) => FavoriteArtist(id = track.user.id,
          username = artist,
          tracks = tracksTail.size + 1,
          avatar_url = track.user.avatar_url)
      } toList
      val sorted = byTracks sortBy (_.tracks)
      sorted.reverse take 10
    }

  }
}
