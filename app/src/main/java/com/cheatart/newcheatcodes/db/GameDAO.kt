package com.cheatart.newcheatcodes.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.room.*
import com.cheatart.newcheatcodes.model.GameData

@Dao
interface GameDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGame(game: Game)


    @Delete
    suspend fun deleteGame(game: Game)

    @Query("SELECT * FROM fav_table")
    fun getAllGames(): LiveData<List<Game>>

    @Query("SELECT id FROM fav_table")
    fun getAllFavIds(): List<Int>
}