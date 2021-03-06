package com.cheatart.newcheatcodes.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheatart.newcheatcodes.data.network.Resource
import com.cheatart.newcheatcodes.data.repository.RawgRepository
import com.cheatart.newcheatcodes.data.response.PopularGamesResponse
import com.cheatart.newcheatcodes.data.response.ScreenShotsResponse
import com.cheatart.newcheatcodes.db.Game
import com.cheatart.newcheatcodes.model.GameData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: RawgRepository
) : ViewModel() {

    private var _screenshotsResponse: MutableLiveData<Resource<ScreenShotsResponse>> =
        MutableLiveData()
    val screenShotsResponse: LiveData<Resource<ScreenShotsResponse>> get() = _screenshotsResponse

    fun getScreenShots(gameId: Int) = viewModelScope.launch {
        _screenshotsResponse.value = Resource.Loading
        _screenshotsResponse.value = repository.getScreenShots(gameId)
    }

    private var _gameDetailsResponse: MutableLiveData<Resource<GameData>> =
        MutableLiveData()
    val gameDetailsResponse: LiveData<Resource<GameData>> get() = _gameDetailsResponse

    fun getGameData(id: Int) = viewModelScope.launch {
        _gameDetailsResponse.value = Resource.Loading
        _gameDetailsResponse.value = repository.getGameDataById(id)
    }


//    private var _developersResponse: MutableLiveData<Resource<GameDevelopersResponse>> =
//        MutableLiveData()
//    val developersResponse: LiveData<Resource<GameDevelopersResponse>> get() = _developersResponse
//
//    fun getGameDevelopers(gameId: Int) = viewModelScope.launch {
//        _developersResponse.value = Resource.Loading
//        _developersResponse.value = repository.getGameDevelopers(gameId)
//    }

//    fun getGameStores(gameId: Int, gameData: GameData) = liveData {
//        val filteredList: MutableList<GameData.Store> = ArrayList()
//        val gameStores = repository.getGameStores(gameId).results
//        for (store: GameData.GameStore in gameStores) {
//            gameData.stores?.forEach {
//                if (store.id == it.store?.id) {
//                    it.store?.url = store.url
//                    filteredList.add(it.store!!)
//                }
//            }
//
//        }
//        emit(filteredList)
//    }

    fun getAllFavIds() = repository.getAllFavIds()

    fun insertGame(game: Game) = viewModelScope.launch {
        repository.insertGame(game)
    }

    fun deleteGame(game: Game) = viewModelScope.launch {
        repository.deleteGame(game)
    }

    fun getAllGames() = repository.getAllGames()


}

