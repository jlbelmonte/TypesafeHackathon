package models

import play.api.libs.json.{Json, Format}

/**
 * Created by gsilin on 10/2/14.
 */


case class UserSummary(id: Long,
                       kind: String,
                       permalink: String,
                       username: String,
                       avatar_url: Option[String],
                       uri: String)

object  UserSummary {
  implicit val format: Format[UserSummary] = Json.format[UserSummary]
}

case class Track(
  id: Long, tag_list: Option[String], created_at: String, favoritings_count: Option[Int], download_count: Option[Int], playback_count: Option[Int], artwork_url: Option[String], uri: String, user: UserSummary
                  )

object  Track {
  implicit val format: Format[Track] = Json.format[Track]
}

/*
* {
id: 887204,
kind: "user",
permalink: "greg-s-1",
username: "greg s",
last_modified: "2014/04/09 17:40:45 +0000",
uri: "https://api.soundcloud.com/users/887204",
permalink_url: "http://soundcloud.com/greg-s-1",
avatar_url: "https://i1.sndcdn.com/avatars-000007898111-4fipl3-large.jpg?86347b7",
country: "United States",
first_name: "greg",
last_name: "silin",
full_name: "greg silin",
description: null,
city: "San Francisco",
discogs_name: null,
myspace_name: null,
website: null,
website_title: null,
online: false,
track_count: 0,
playlist_count: 9,
plan: "Free",
public_favorites_count: 774,
followers_count: 119,
followings_count: 497,
subscriptions: [ ]
}*/
case class User(id: Long, kind: String, username: String, permalink_url: String, avatar_url:String, public_favorites_count: Option[Int], fullname: Option[String], description:Option[String])

object User {
  implicit val format: Format[User] = Json.format[User]
}


/*
{
"kind": "track",
"id": 170355785,
"created_at": "2014/10/02 18:25:59 +0000",
"user_id": 103784,
"duration": 5256146,
"commentable": true,
"state": "finished",
"original_content_size": 210235447,
"last_modified": "2014/10/02 19:22:41 +0000",
"sharing": "public",
"tag_list": ""hot natured" "pleasure state" "all vinyl" "emerald city" "jamie jones" mk "anabel englund"",
"permalink": "lee-foss-mysterylands-2014-all-vinyl-set",
"streamable": true,
"embeddable_by": "all",
"downloadable": true,
"purchase_url": null,
"label_id": null,
"purchase_title": null,
"genre": "hot creations",
"title": "Lee Foss Mysterylands 2014 All Vinyl Set",
"description": "here is my all vinyl set from mysterylands at the original woodstock festival site in upstate ny in may.\nhadn't played an all vinyl set in 8 years, so it was cool go back through records and to buy some new ones. not sure i'd want to lug them around all the time but it wont be my last all vinyl set and glad there was such a positive response.",
"label_name": null,
"release": null,
"track_type": null,
"key_signature": null,
"isrc": null,
"video_url": null,
"bpm": null,
"release_year": null,
"release_month": null,
"release_day": null,
"original_format": "mp3",
"license": "all-rights-reserved",
"uri": "https://api.soundcloud.com/tracks/170355785",
"user":  {
"id": 103784,
"kind": "user",
"permalink": "leefoss",
"username": "Lee Foss",
"last_modified": "2014/09/29 05:03:35 +0000",
"uri": "https://api.soundcloud.com/users/103784",
"permalink_url": "http://soundcloud.com/leefoss",
"avatar_url": "https://i1.sndcdn.com/avatars-000039471757-07dmva-large.jpg?86347b7"
},
"permalink_url": "http://soundcloud.com/leefoss/lee-foss-mysterylands-2014-all-vinyl-set",
"artwork_url": "https://i1.sndcdn.com/artworks-000092763321-7h61w9-large.jpg?86347b7",
"waveform_url": "https://w1.sndcdn.com/RqV7hYuFRcEQ_m.png",
"stream_url": "https://api.soundcloud.com/tracks/170355785/stream",
"download_url": "https://api.soundcloud.com/tracks/170355785/download",
"playback_count": 922,
"download_count": 80,
"favoritings_count": 107,
"comment_count": 4,
"attachments_uri": "https://api.soundcloud.com/tracks/170355785/attachments",
"policy": "ALLOW"
},
*/