package com.cheatart.newcheatcodes.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheatart.newcheatcodes.data.network.Resource
import com.cheatart.newcheatcodes.data.repository.CheatRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheatViewModel @Inject constructor(private val cheatRepository: CheatRepository) :
    ViewModel() {

    private var _pcResponse: MutableLiveData<Resource<Task<DataSnapshot>>> = MutableLiveData()
    val pcResponse: LiveData<Resource<Task<DataSnapshot>>> get() = _pcResponse

    fun getPcCheats() = viewModelScope.launch {
        _pcResponse.value = Resource.Loading
        _pcResponse.value = cheatRepository.getPcCheats()
    }

    private var _psResponse: MutableLiveData<Resource<Task<DataSnapshot>>> = MutableLiveData()
    val psResponse: LiveData<Resource<Task<DataSnapshot>>> get() = _psResponse

    fun getPsCheats() = viewModelScope.launch {
        _psResponse.value = Resource.Loading
        _psResponse.value = cheatRepository.getPsCheats()
    }


    private var _xboxResponse: MutableLiveData<Resource<Task<DataSnapshot>>> = MutableLiveData()
    val xboxResponse: LiveData<Resource<Task<DataSnapshot>>> get() = _xboxResponse

    fun getXboxCheats() = viewModelScope.launch {
        _xboxResponse.value = Resource.Loading
        _xboxResponse.value = cheatRepository.getXboxCheats()
    }
}