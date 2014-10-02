package services

import nitroz.futures._
import play.api.Play.current
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import models.{FavoriteArtist, FavoritesSummary, Track, User}
import play.api.libs.json.{Reads, JsError, JsSuccess, Json}
import play.api.libs.ws.{WSRequestHolder, WSRequest, WS}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
 * Created by gsilin on 10/2/14.
 */
trait SoundCloudServiceComponent {

  val SoundCloudKey: String = "a24c214fe3495b5a8d8ad56bd5fadbc7"

  val BaseUrl: String = "https://api.soundcloud.com"

  def withUrl(url: String) = WS.url(url).withQueryString(("consumer_key", SoundCloudKey))


  val soundCloudService: SoundCloudService
  trait SoundCloudService {

    def soundCloudRequest[T: Reads](req: WSRequestHolder): Future[T]
    def soundCloudRequest[T: Reads](url: String): Future[T]

    def getFavoriteTracks(username: String, paginate: Boolean = true): Future[List[Track]]
    def getFavoriteTracksBatch(usernames: List[String]): Future[Map[String, List[Track]]]

    def getFavoritesSummary(username: String): Future[Option[FavoritesSummary]]

    def getFollowings(id: String): Future[List[User]]
    def getFollowingsFollowings(id: String): Future[List[User]]
  }

}

trait RealSoundCloudServiceComponent extends SoundCloudServiceComponent {

  lazy val soundCloudService = new SoundCloudService {

    def paginated[T: Reads](url: String): Future[List[T]] = {
      def paginatedHelper(offset: Int, limit: Int, soFar: List[T]): Future[List[T]] = {
        for {
          page <- {
            println(s"Geting offset $offset limit $limit so far ${soFar.size}")
            soundCloudRequest[List[T]](withUrl(url).withQueryString("limit" -> limit.toString, "offset" -> offset.toString))
          }
          _ = println("page size " + page.size)
          rest <- page match {
            case head :: tail => {
              paginatedHelper(offset + limit, limit, soFar ++ page)
            }
            case Nil => sync(soFar)
          }
        } yield rest
      }

      paginatedHelper(0, 200, Nil)
    }


    override def soundCloudRequest[T: Reads](url: String) = soundCloudRequest(withUrl(url))
    override def soundCloudRequest[T: Reads](req: WSRequestHolder): Future[T] = {
      for {
        response <- req.get()
      } yield {
        println("Response: " + response.status)
        Try {
          Json.fromJson[T](response.json) match {
            case JsSuccess(value, _) => value
            case JsError(errors) =>
              println(s"Fail!!! \n${errors.mkString("\n")}")
              throw new Exception(s"Fail!!! \n${errors.mkString("\n")}")
          }
        } match {
          case Success(value) => value
          case Failure(e) => println(s"ERROR: ${e.getMessage}\n${response.body}")
            throw e
        }
      }
    }

    override def getFollowings(id: String): Future[List[User]] = {
      paginated[User](s"$BaseUrl/users/$id/followings.json")
    }

    override def getFollowingsFollowings(id: String): Future[List[User]] = {
      for {
        followed <- getFollowings(id)
        all <- getAll(List(for (f <- followed) yield f.id.toString).flatten)
      } yield {
        val count: Map[User, Int] = followed.foldRight(Map[User, Int]()) ((user, acc) => {
          if (followed.contains(user)) {
            acc
          } else {
            acc.get(user) match {
              case Some(i) =>  acc + (user -> (1+i))
              case _ =>  acc + (user -> 1)
            }
          }
        })
        val ordered = count.toList.sortBy(_._2).reverse
        ordered.take(10).unzip._1
      }
    }

    def getAll(ids: Seq[String]): Future[List[User]] = {
      def getChunk(uids: Seq[String]): Seq[Future[User]] = {
        val result = for (uid <- uids) yield soundCloudRequest[User](s"$BaseUrl/users/$uid/following.json")
        // lets avoid getting banned
        Thread sleep 1000
        result
      }
      val groups = ids.grouped(5).toList
      val listOfSeq = for (g <- groups) yield (getChunk(g))
      Future.sequence(listOfSeq.foldLeft(List[Future[User]]()) { (l: List[Future[User]], s) => l ++ s.toList})
    }

    override def getFavoriteTracks(username: String, paginate: Boolean = true): Future[List[Track]] = {
      if (paginate)
        paginated[Track](s"$BaseUrl/users/$username/favorites.json")
      else
        soundCloudRequest[List[Track]](s"$BaseUrl/users/$username/favorites.json")
    }

    override def getFavoriteTracksBatch(usernames: List[String]): Future[Map[String, List[Track]]] = {
      val batched = usernames.grouped(5).toList
      val traversed = Future.traverse(batched) { batch =>
//        Thread.sleep(100L)
        getFavoriteTracksHelper(batch)
      }
      traversed map { bs =>
        bs.foldLeft(Map.empty[String, List[Track]])(_ ++ _)
      }
    }

    private def getFavoriteTracksHelper(usernames: List[String]): Future[Map[String, List[Track]]] = {
      val futures = usernames map { username =>
          println(s"getting tracks for $username")
//        Thread.sleep(50L)
        getFavoriteTracks(username, paginate = false) map (tracks => username -> tracks)
      }
      Future.sequence(futures) map (_.toMap)
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
        avatar_url = track.user.avatar_url,
        uri = track.user.uri)
      } toList
      val sorted = byTracks sortBy (_.tracks)
      sorted.reverse take 10
    }

  }


}