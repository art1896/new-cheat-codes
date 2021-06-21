package com.cheatart.newcheatcodes.data.response

import com.cheatart.newcheatcodes.model.GameData


data class ScreenShotsResponse(
    val results: List<GameData.ScreenShot>
)