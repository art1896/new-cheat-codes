package com.cheatart.newcheatcodes.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cheatart.newcheatcodes.data.api.RawgApi
import com.cheatart.newcheatcodes.model.GameData
import retrofit2.HttpException
import java.io.IOException


private const val STARTING_PAGE_INDEX = 1

class GamePagingSource(
    private val fromSearch: Boolean,
    private val rawgApi: RawgApi,
    private val platformsId: String? = "",
    private val ordering: String? = "",
    private val genres: String? = "",
    private val searchQuery: String? = ""
) : PagingSource<Int, GameData>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GameData> {
        val position = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = if (fromSearch) rawgApi.getGames(
                pageSize = params.loadSize,
                page = position,
                platformsId = platformsId,
                ordering = ordering,
                genres = genres,
                searchQuery = searchQuery
            ) else rawgApi.getPopularGames(
                pageSize = params.loadSize,
                page = position
            )
            val games = response.results

            LoadResult.Page(
                data = games,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (games.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, GameData>): Int? {
        return null
    }

}