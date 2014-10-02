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

  lazy val recommendationService: RecommendationService = new RecommendationService {
    def recommendFavorites(username: String): Future[List[User]] = sync(Nil)

  }
}
