package services

import models.{FavoriteArtist, FavoritesSummary, Track, User}
import nitroz.futures._
import play.api.Play.current
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import play.api.libs.ws.WS

import scala.concurrent.Future

/**
 * Created by gsilin on 10/2/14.
 */
trait SoundCloudServiceComponent {

  val SoundCloudKey: String = "a24c214fe3495b5a8d8ad56bd5fadbc7"

  val BaseUrl: String = "https://api.soundcloud.com"

  def withUrl(url: String) = WS.url(url).withQueryString(("consumer_key", SoundCloudKey))


  val soundCloudService: SoundCloudService
  trait SoundCloudService {

    def soundCloudRequest[T: Reads](url: String): Future[T]

    def getFavoriteTracks(username: String): Future[List[Track]]

    def getFavoritesSummary(username: String): Future[Option[FavoritesSummary]]

    def getFollowings(id: String): Future[List[User]]
  }

}

trait RealSoundCloudServiceComponent extends SoundCloudServiceComponent {

  lazy val soundCloudService = new SoundCloudService {

    override def soundCloudRequest[T: Reads](url: String): Future[T] = {
      for {
        response <- withUrl(url).get()
      } yield {

        println(s"RESPONSE: \n${response.status}")
        println(s"RESPONSE: \n${response.body}")
        Json.fromJson[T](response.json) match {
          case JsSuccess(value, _) => value
          case JsError(errors) =>
            println(s"Fail!!! \n${errors.mkString("\n")}")
            throw new Exception(s"Fail!!! \n${errors.mkString("\n")}")
        }
      }
    }

    override def getFollowings(id: String): Future[List[User]] = {
      soundCloudRequest[List[User]](s"$BaseUrl/users/$id/followings.json")
    }


    def getUser(id: String): Future[User] = {
      soundCloudRequest[User](s"$BaseUrl/users/$id.json")
    }

    def getAll(ids: String*): Future[List[User]] = {
      def getChunk(uids: Seq[String]): Seq[Future[User]] = {
        val result = for (uid <- uids) yield getUser(uid)
        // lets avoid getting banned
        Thread sleep 1000
        result
      }
      val groups = ids.grouped(5).toList
      val listOfSeq = for (g <- groups) yield (getChunk(g))
      Future.sequence(listOfSeq.foldLeft(List[Future[User]]()) { (l: List[Future[User]], s) => l ++ s.toList})
    }

    override def getFavoriteTracks(username: String): Future[List[Track]] = {
      soundCloudRequest[List[Track]](s"$BaseUrl/users/$username/favorites.json")
    }

    def getFavoritesSummary(username: String): Future[Option[FavoritesSummary]] = {
      for {
        favorites <- getFavoriteTracks(username)
      } yield {
        //(id: Long, username: String, total: Int, topArtists: List[FavoriteArtist] )
        Some(FavoritesSummary(
          0L/*todo*/,
          username = username,
          totalFavorites = favorites.size /*todo*/,
          topArtists =  faveArtists(favorites)))
      }
    }

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