package com.cheatart.newcheatcodes.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Game::class],
    version = 1
)
abstract class GameDatabase: RoomDatabase() {

    abstract fun getGameDao(): GameDAO

}