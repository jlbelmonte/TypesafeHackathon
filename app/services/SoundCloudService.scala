package services

import models.Track
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.WS

import scala.concurrent.Future
import nitroz.futures._
import play.api.Play.current

/**
 * Created by gsilin on 10/2/14.
 */
trait SoundCloudServiceComponent {

  val SoundCloudKey: String = "a24c214fe3495b5a8d8ad56bd5fadbc7"

  val BaseUrl: String = "https://api.soundcloud.com"

  def withUrl(url: String) = WS.url(url).withQueryString(("consumer_key", SoundCloudKey))


  val soundCloudService: SoundCloudService
  trait SoundCloudService {

    def getFavoriteTracks(username: String): Future[List[Track]]
  }

}

trait RealSoundCloudServiceComponent extends SoundCloudServiceComponent {

  lazy val soundCloudService = new SoundCloudService {

    override def getFavoriteTracks(username: String): Future[List[Track]] = {
      for {
        response <- withUrl(s"$BaseUrl/users/$username/favorites.json").get()

      } yield {
        println(s"RESPONSE: \n${response.status}")
        println(s"RESPONSE: \n${response.body}")
        Json.fromJson[List[Track]](response.json) match {
          case JsSuccess(value,_) => value
          case JsError(errors) =>
            println(s"Fail!!! \n${errors.mkString("\n")}")
            throw new Exception(s"Fail!!! \n${errors.mkString("\n")}"  )
        }
      }
    }
  }
}