package com.dangerfield.gitjob.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dangerfield.gitjob.model.JobListing
import com.dangerfield.gitjob.model.SavedJob
import com.dangerfield.gitjob.model.SearchedTerm

@Dao
interface JobListingsDao {

    /*---------------------JOB LISTINGS----------------------*/

    /**
     * returns all job listings in table
     */
    @Query("SELECT * from JOB_LISTINGS")
    fun getAll(): LiveData<List<JobListing>>

    /**
     * inserts all passed job listings into database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(articles: List<JobListing>)

    /**
     * removes all job listings in the database
     */
    @Query("DELETE from JOB_LISTINGS")
    fun deleteAll()

    /**
     * replaces all data in database
     */
    @Transaction
    fun updateAll(articles: List<JobListing>) {
        deleteAll()
        insertAll(articles)
    }


    /*---------------------SAVED JOB LISTINGS----------------------*/

    /**
     * gets all job listings that have been saved
     */
    @Query("SELECT * from SAVED_JOB_LISTINGS")
    fun getAllSavedJobs(): LiveData<List<SavedJob>>

    /**
     * saves a job listing
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSavedJob(savedJob: SavedJob)

    /**
     * queries for a saved job with a job id
     */
    @Query("SELECT * from SAVED_JOB_LISTINGS WHERE id = :withID")
    suspend fun querySavedJob(withID: String): List<SavedJob>

    /**
     * deletes a saved job
     */
    @Query("DELETE from SAVED_JOB_LISTINGS WHERE id = :withID")
    fun deleteSavedJob(withID: String)


    /*---------------------SEARCHED TERMS----------------------*/

    /**
     * retrieves all searched terms
     */
    @Query("SELECT * from SEARCHED_TERMS")
    fun getAllSearchedTerms(): LiveData<List<SearchedTerm>>

    /**
     * inserts a searched term into the database
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSearchedTerm(term: SearchedTerm)

    /**
     * deletes a searched term
     */
    @Query("DELETE from SEARCHED_TERMS WHERE term = :term")
    fun deleteSearchedTerm(term: String)

}