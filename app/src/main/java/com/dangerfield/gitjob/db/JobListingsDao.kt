package com.dangerfield.gitjob.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.model.SavedJob

@Dao
interface JobListingsDao {

    /**
     * returns all articles in table
     */
    @Query("SELECT * from JOB_LISTINGS")
    fun getAll(): LiveData<List<JobListing>>

    /**
     * inserts all passed articles into database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(articles: List<JobListing>)


    @Query("SELECT * from SAVED_JOB_LISTINGS")
    fun getAllSavedJobs(): LiveData<List<SavedJob>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSavedJob(savedJob: SavedJob)

    @Query("SELECT * from SAVED_JOB_LISTINGS WHERE id = :withID")
    fun querySavedJob(withID: String): List<SavedJob>

    @Query("DELETE from SAVED_JOB_LISTINGS WHERE id = :withID")
    fun deleteSavedJob(withID: String)

    /**
     * removes all articles in the database
     */
    @Query("DELETE from JOB_LISTINGS")
    fun deleteAll()

    /**
     * replaces all data in database. Chosen to do this rather than
     * only replacing on conflict because as time goes on, users will retain
     * ALL articles ever seen. Data will accumulate. This will only hold on to the
     * most recent data :)
     */
    @Transaction
    fun updateAll(articles: List<JobListing>) {
        deleteAll()
        insertAll(articles)
    }
}