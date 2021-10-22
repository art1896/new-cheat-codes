package com.cheatart.newcheatcodes.di

import android.content.Context
import androidx.room.Room
import com.cheatart.newcheatcodes.data.api.RawgApi
import com.cheatart.newcheatcodes.db.GameDatabase
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGameDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        GameDatabase::class.java,
        "games_db"
    )
        .allowMainThreadQueries()
        .build()

    @Provides
    @Singleton
    fun provideGameDao(db: GameDatabase) = db.getGameDao()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(RawgApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRawgApi(retrofit: Retrofit): RawgApi =
        retrofit.create(RawgApi::class.java)
}
