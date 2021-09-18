package com.benmedcalf.alltrailshomework

import android.content.Context
import androidx.room.Room
import com.benmedcalf.alltrailshomework.model.local.PlacesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Singleton
    @Provides
    fun providePlacesDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            PlacesDatabase::class.java,
            "places-database"
        ).build()

    @Singleton
    @Provides
    fun providePlacesDao(@ApplicationContext context: Context) =
        providePlacesDatabase(context).placeDao()
}