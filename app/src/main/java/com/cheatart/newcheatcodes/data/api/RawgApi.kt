package com.cheatart.newcheatcodes.data.api

import com.cheatart.newcheatcodes.data.response.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RawgApi {

    companion object {
        const val BASE_URL = "https://api.rawg.io/api/"
    }

    @GET("games/lists/greatest")
    suspend fun getPopularGames(
        @Query("year") year: String = "2020",
        @Query("key") key: String = "c542e67aec3a4340908f9de9e86038af",
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): PopularGamesResponse


    @GET("games")
    suspend fun getGames(
        @Query("key") key: String = "c542e67aec3a4340908f9de9e86038af",
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("platforms") platformsId: String?,
        @Query("ordering") ordering: String?,
        @Query("genres") genres: String?,
        @Query("search") searchQuery: String?
    ): PopularGamesResponse

    @GET("games/{id}/screenshots")
    suspend fun getGameScreenShots(
        @Path("id") gameId: Int,
        @Query("key") key: String = "c542e67aec3a4340908f9de9e86038af"
    ): ScreenShotsResponse

    @GET("games/{id}/stores")
    suspend fun getGameStores(
        @Path("id") gameId: Int,
        @Query("key") key: String = "c542e67aec3a4340908f9de9e86038af"
    ): GameStoresResponse

    @GET("games/{id}")
    suspend fun getGameDevelopers(
        @Path("id") gameId: Int,
        @Query("key") key: String = "c542e67aec3a4340908f9de9e86038af"
    ): GameDevelopersResponse

    @GET("genres")
    suspend fun getGenres(
        @Query("key") key: String = "c542e67aec3a4340908f9de9e86038af"
    ): GenresResponse


    @GET("platforms")
    suspend fun getPlatforms(
        @Query("key") key: String = "c542e67aec3a4340908f9de9e86038af"
    ): PlatformsResponse
}
