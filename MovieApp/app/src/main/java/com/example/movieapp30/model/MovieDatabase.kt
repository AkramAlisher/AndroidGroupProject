package com.example.movieapp30.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Movie::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {

        var INSTANCE: MovieDatabase? = null

        fun getDatabase(context: Context?): MovieDatabase {
            if (INSTANCE == null) {
                INSTANCE = context?.applicationContext?.let {
                    Room.databaseBuilder(
                        it,
                        MovieDatabase::class.java,
                        "movie_database.db"
                    ).build()
                }
            }
            return INSTANCE as MovieDatabase
        }

    }
}