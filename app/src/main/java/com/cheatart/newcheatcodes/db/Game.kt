package com.cheatart.newcheatcodes.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.cheatart.newcheatcodes.model.Platform

@Entity(tableName = "fav_table")
data class Game(
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null,
    var gameName: String?,
    var gameImage: String?,
)