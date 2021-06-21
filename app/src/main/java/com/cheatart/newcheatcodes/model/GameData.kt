package com.cheatart.newcheatcodes.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameData(
    val id: Int?,
    val slug: String?,
    val name: String?,
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
    val genres: List<Genre>?
) : Parcelable {

    @Parcelize
    data class Genre(
        val id: Int?,
        val name: String?,
        val slug: String?,
    ) : Parcelable

    @Parcelize
    data class Stores(
        val store: Store?
    ) : Parcelable

    @Parcelize
    data class Developer(
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
    data class GameStore(
        @SerializedName("store_id")
        val id: Int?,
        val url: String?
    ) : Parcelable

    @Parcelize
    data class Platform(
        val platform: Platforms?
    ) : Parcelable

    @Parcelize
    data class ScreenShot(
        val image: String?
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
        val clip: String?
    ) : Parcelable


    @Parcelize
    data class Game(
        val id: Int?,
        val slug: String?,
        val name: String?
    ) : Parcelable

}