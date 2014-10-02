package nitroz

import org.joda.time.DateTime
import play.api.libs.json.{Json, Writes}
import play.api.mvc._
import play.api.mvc.Results._
import play.api.mvc.BodyParsers.parse
import nitroz.futures._


import scala.concurrent.Future

/**
 * Created by gsilin on 10/2/14.
 */
trait TypesafeController extends Controller with Actions

trait Actions {

  def asyncAction[A, T: Writes](parser: BodyParser[A] = parse.anyContent)(work: Request[A] => Future[T]) = Action.async(parser) { implicit request =>

   toResultAsync(work(request))
  }

  def toResultAsync[T: Writes](resultFuture: Future[T]): Future[Result] = {
    val start = new DateTime
    resultFuture map { result =>
      val json = Json toJson result
      val finish = new DateTime()
      val took = finish.getMillis - start.getMillis
      println(s"It took $took milliseconds, or ${took / 1000} seconds")
      Results.Ok(json)
    }
  }
}

