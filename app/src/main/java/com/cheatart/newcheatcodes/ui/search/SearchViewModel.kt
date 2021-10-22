package com.cheatart.newcheatcodes.ui.search

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cheatart.newcheatcodes.data.repository.RawgRepository
import com.cheatart.newcheatcodes.data.response.GenresResponse
import com.cheatart.newcheatcodes.data.response.PlatformsResponse
import com.cheatart.newcheatcodes.model.GameData
import com.cheatart.newcheatcodes.model.QueryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: RawgRepository
) : ViewModel() {

    private var _platformsResponse: MutableLiveData<PlatformsResponse> = MutableLiveData()
    val platformsResponse: LiveData<PlatformsResponse> get() = _platformsResponse

    fun getPlatforms() = viewModelScope.launch {
        _platformsResponse.value = repository.getPlatforms()
    }

    private var _genresResponse: MutableLiveData<GenresResponse> = MutableLiveData()
    val genresResponse: LiveData<GenresResponse> get() = _genresResponse

    fun getGenres() = viewModelScope.launch {
        _genresResponse.value = repository.getGenres()
    }
//
//    private var _popularGamesResponse: LiveData<PagingData<GameData>> =
//        MutableLiveData()
//    val popularGamesResponse: LiveData<PagingData<GameData>> get() = _popularGamesResponse

    private val currentQuery = MutableLiveData(
        QueryModel(
            DEFAULT_PLATFORM_IDS,
            DEFAULT_ORDERING,
            DEFAULT_GENRES,
            DEFAULT_QUERY
        )
    )

    val games = currentQuery.switchMap { queryModel ->
        repository.getGames(
            queryModel.platformsId,
            queryModel.ordering,
            queryModel.genres,
            queryModel.searchQuery
        ).cachedIn(viewModelScope)
    }


    fun getGames(
        platformsId: String?,
        ordering: String?,
        genres: String?,
        searchQuery: String?
    ) {
        currentQuery.value = QueryModel(platformsId, "-$ordering", genres, searchQuery)
    }

    companion object {
        const val DEFAULT_QUERY = "ajkhsdkasdkjasd"
        val DEFAULT_PLATFORM_IDS = null
        const val DEFAULT_ORDERING = "-added"
        val DEFAULT_GENRES = null

    }

}