package models

import play.api.libs.json.{Json, Format}

/**
 * Created by gsilin on 10/2/14.
 */

case class FavoritesSummary(id: Long, username: String, totalFavorites: Int, topArtists: List[FavoriteArtist] )

object FavoritesSummary {
  implicit val format: Format[FavoritesSummary] = Json.format[FavoritesSummary]
}

case class FavoriteArtist(id: Long, username: String, tracks: Int, avatar_url: Option[String], uri: String)

object FavoriteArtist {
  implicit val format: Format[FavoriteArtist] = Json.format[FavoriteArtist]
}

