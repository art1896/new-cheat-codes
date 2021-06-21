package com.cheatart.newcheatcodes.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.cheatart.newcheatcodes.data.repository.RawgRepository
import com.cheatart.newcheatcodes.model.GameData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularsViewModel @Inject constructor(private val rawgRepository: RawgRepository) :
    ViewModel() {

    private var _popularGamesResponse: LiveData<PagingData<GameData>> =
        MutableLiveData()
    val popularGamesResponse: LiveData<PagingData<GameData>> get() = _popularGamesResponse

    fun getPopularGames() = viewModelScope.launch {
        _popularGamesResponse = rawgRepository.getPopularGames()
    }


}