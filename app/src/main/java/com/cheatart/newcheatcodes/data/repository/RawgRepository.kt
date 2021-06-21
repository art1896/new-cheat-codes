package com.cheatart.newcheatcodes.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.cheatart.newcheatcodes.data.GamePagingSource
import com.cheatart.newcheatcodes.data.api.RawgApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RawgRepository @Inject constructor(private val rawgApi: RawgApi) : BaseRepository() {


    suspend fun getScreenShots(gameId: Int) = safeApiCall { rawgApi.getGameScreenShots(gameId) }

    suspend fun getGameStores(gameId: Int) = rawgApi.getGameStores(gameId)

    suspend fun getGameDevelopers(gameId: Int) = safeApiCall { rawgApi.getGameDevelopers(gameId) }

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