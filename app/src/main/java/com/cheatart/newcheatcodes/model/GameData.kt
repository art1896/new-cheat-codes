package com.cheatart.newcheatcodes.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameData(
    val id: Int?,
    val slug: String?,
    val name: String?,
    @SerializedName("description_raw")
    val description: String?,
    val released: String?,
    val background_image: String?,
    val rating: Double?,
    val metacritic: Int?,
    val rating_top: Int?,
    val ratings_count: Int?,
    val clip: Clip?,
    val playtime: String?,
    var stores: List<Stores>?,
    var screenShot: List<ScreenShot>?,
    val platforms: List<Platform>?,
    @SerializedName("esrb_rating")
    val esrbRating: EsrbRating?,
    val genres: List<Genre>?,
    val publishers: List<Publisher>?,
    val tags: List<Tag>?,
    val developers: List<Developer>?,
    val website: String?,
    @SerializedName("parent_platforms")
    val parentPlatforms: List<Platform>?,
    val ratings: List<Rating>?
) : Parcelable

@Parcelize
data class Rating(
    val title: String?,
    val count: Int?,
    val percent: Double?,
    var color: Int?
): Parcelable

@Parcelize
data class Tag(
    val id: Int?,
    val name: String?
) : Parcelable

@Parcelize
data class Publisher(
    val id: Int?,
    val name: String?
) : Parcelable

@Parcelize
data class EsrbRating(
    val id: Int?,
    val name: String?
) : Parcelable

@Parcelize
data class Genre(
    val id: Int?,
    val name: String?,
    val slug: String?,
) : Parcelable

@Parcelize
data class Stores(
    val url: String?,
    val store: Store?
) : Parcelable

@Parcelize
data class Developer(
    val id: Int?,
    val name: String?
) : Parcelable

@Parcelize
data class Store(
    val id: Int?,
    val name: String?,
    var image: Int?,
    var url: String?
) : Parcelable


@Parcelize
data class Platform(
    val platform: Platforms?
) : Parcelable

@Parcelize
data class ScreenShot(
    val image: String?,
    var clip: String?
) : Parcelable

@Parcelize
data class Platforms(
    val id: Int?,
    val name: String?,
    val image_background: String?,
    val games_count: Int?,
    val games: List<Game>?
) : Parcelable

@Parcelize
data class Clip(
    val clip: String?,
    @SerializedName("video")
    val _video: String?
) : Parcelable {
    val video: String
        get() = "https://www.youtube.com/watch?v=$_video"
}


@Parcelize
data class Game(
    val id: Int?,
    val slug: String?,
    val name: String?
) : Parcelable
