package com.cheatart.newcheatcodes.data.response

import android.os.Parcelable
import com.cheatart.newcheatcodes.model.GameData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PopularGamesResponse(
    val results: List<GameData>
) : Parcelable