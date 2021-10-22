package com.cheatart.newcheatcodes.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.cheatart.newcheatcodes.data.GamePagingSource
import com.cheatart.newcheatcodes.data.api.RawgApi
import com.cheatart.newcheatcodes.db.Game
import com.cheatart.newcheatcodes.db.GameDAO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RawgRepository @Inject constructor(private val rawgApi: RawgApi, private val gameDAO: GameDAO) : BaseRepository() {


    suspend fun insertGame(game: Game) = gameDAO.insertGame(game)

    suspend fun deleteGame(game: Game) = gameDAO.deleteGame(game)

    fun getAllGames() = gameDAO.getAllGames()

    fun getAllFavIds() = gameDAO.getAllFavIds()

    suspend fun getScreenShots(gameId: Int) = safeApiCall { rawgApi.getGameScreenShots(gameId) }

    suspend fun getGameDataById(id: Int) = safeApiCall { rawgApi.getGameDataById(id) }

//    suspend fun getGameStores(gameId: Int) = rawgApi.getGameStores(gameId)

//    suspend fun getGameDevelopers(gameId: Int) = safeApiCall { rawgApi.getGameDevelopers(gameId) }

    suspend fun getPlatforms() = rawgApi.getPlatforms()

    suspend fun getGenres() = rawgApi.getGenres()

    fun getGames(
        platformsId: String?,
        ordering: String?,
        genres: String?,
        searchQuery: String?
    ) =
        Pager(
            config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
            pagingSourceFactory = {
                GamePagingSource(
                    true,
                    rawgApi,
                    platformsId,
                    ordering,
                    genres,
                    searchQuery
                )
            }).liveData

    fun getPopularGames() =
        Pager(
            config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
            pagingSourceFactory = {
                GamePagingSource(
                    false,
                    rawgApi
                )
            }).liveData
}