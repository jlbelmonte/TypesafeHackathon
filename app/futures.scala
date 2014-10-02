package nitroz

import play.api.libs.concurrent.Execution.Implicits
import scala.concurrent._

/**
 * creates a package async with default synonymous object.
 */
package object futures {
  implicit val context: ExecutionContext = Implicits.defaultContext

  /**
   * Shorthand wrapper for synchronous (blocking) piece of work wrapped as a future.
   * @param blocking
   * @tparam T
   * @return
   */
  def sync[T](blocking: => T) = Future.successful(blocking)

  /**
   * Shorthand for promise returning nothing.
   */
  def sync = sync[Unit](())

  /**
   * Shorthand wrapper for asynchronous (non-blocking) piece of work wrapped as a future.
   * @param parallel
   * @tparam T
   * @return
   */
  def async[T](parallel: => T) = Future {
    parallel
  }
}