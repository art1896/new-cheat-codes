package com.cheatart.newcheatcodes.data.repository

import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheatRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) :
    BaseRepository() {

    companion object {
        const val PS = "PS4"
        const val PC = "PC"
        const val XBOX = "Xbox"
    }

    suspend fun getPsCheats() = safeApiCall {
        firebaseDatabase.getReference(PS).get()
    }

    suspend fun getPcCheats() = safeApiCall {
        firebaseDatabase.getReference(PC).get()
    }

    suspend fun getXboxCheats() = safeApiCall {
        firebaseDatabase.getReference(XBOX).get()
    }
}