package com.dangerfield.gitjob.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.model.SavedJob
import com.dangerfield.gitjob.model.SearchedTerm

@Database(entities = [JobListing::class, SavedJob::class, SearchedTerm::class], version = 5, exportSchema = false)
abstract class GitJobDatabase : RoomDatabase() {
    abstract fun mainDao(): JobListingsDao

    companion object {
        @Volatile private var instance: GitJobDatabase? = null
        private val LOCK = Any()

        /**
         * using invoke will make it very simple to create a database instance
         * val db = ArticlesDatabase(context)
         */
        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            GitJobDatabase::class.java, "articles.db")
            .build()
    }
}