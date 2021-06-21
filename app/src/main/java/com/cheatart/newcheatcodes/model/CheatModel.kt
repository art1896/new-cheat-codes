package com.cheatart.newcheatcodes.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CheatModel(
    val description: String = "",
    val name: String = "",
    val platform: String = "",
    val poster: String = "",
    val codes: List<Code> = ArrayList()
) : Parcelable


@Parcelize
data class Code(
    val code: String = "",
    val result: String = ""
) : Parcelable