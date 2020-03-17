package com.dangerfield.gitjob.api

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dangerfield.gitjob.model.JobListing

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */
abstract class NetworkBoundResource<ResultType, CallParameters> {

    private val result = MediatorLiveData<Resource<ResultType>>()

    /*
    immediately shows data from database, if it should fetch then it gets new data else
    it keeps the stream to the database
     */
    //TODO: the db source is added twice. Not sure if this is necessary
    fun build(params: CallParameters): NetworkBoundResource<ResultType, CallParameters> {
        result.value = Resource.Loading(null)
        val dbSource = loadFromDb()
        @Suppress("LeakingThis")
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource, params) //at this point there are 0 sources
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(Resource.Success(newData))
                    result.removeSource(dbSource) // immediately remove source
                }
            }
        }

        return this
    }

    fun refresh(params: CallParameters): NetworkBoundResource<ResultType, CallParameters> {
        val apiResponse = createCall(params)

        setValue(Resource.Loading())

        result.addSource(apiResponse) { response ->
            //remove the source on the first response
            result.removeSource(apiResponse)


            when(response) {
                //When it is successful, immediately store results.
                // Then tell result to load from db as success
                is ApiResponse.Success -> {
                    Log.d("Elijah","Got success in network bound resource refresh")

                    var count = 0
                    (response.data!! as List<JobListing>).forEach { if(it.saved == true) count++ }
                    Log.d("Elijah", "saving call with $count items marked as saved")


                    saveCallResult(response.data!!)
                    /*
                    So the problem with the searching and with the saving/hearting happens here.
                    I am saving the call and then loading from the db expecting those changes to be there
                    or at least propagte to it. But they arent. Loading from the db uses the previous data.
                    what was there before the saved call. When I refresh again i loads from db again and then has the right data.
                    Im assuming this is either some weird thing with live data or a race condition where it doesnt get saved in time
                    and for some reason the call doesnt propagate.

                    tomorrow: remove all the saving logic and just test with searching and such.
                     */

                    val dbSource = loadFromDb()
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.Success(newData))
                        result.removeSource(dbSource)
                    }
                }

                is ApiResponse.Empty -> {
                    //when it is empty, thats fine for our purposes, so pull from db as success
                    Log.d("Elijah","Got empty in network bound resource")
                    setValue(Resource.Error(message = response.message!!))
                    val dbSource = loadFromDb()

                    result.addSource(dbSource) { newData ->
                        setValue(Resource.Error(data = newData, message = response.message, errorType = response.errorType))
                        result.removeSource(dbSource)
                    }
                }
                //when it is an error, return as error
                is ApiResponse.Error -> {
                    Log.d("Elijah","Got error in network bound resource")

                    val dbSource = loadFromDb()

                    result.addSource(dbSource) { newData ->
                        setValue(Resource.Error(newData , response.message ?: "unknown error"))
                        result.removeSource(dbSource)
                    }
                }
            }
        }

        return this
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>, params: CallParameters) {
        val apiResponse = createCall(params)
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            Log.d("Elijah","set loading in network bound resource")

            setValue(Resource.Loading(newData))
            //While fetching, show old data from db
        }

        // at this point, result has 1 source

        result.addSource(apiResponse) { response ->
            //remove the source on the first response
            result.removeSource(apiResponse)
            result.removeSource(dbSource)

            //back to zero sources

            when(response) {
                //When it is successful, immediately store results.
                // Then tell result to load from db as success
                is ApiResponse.Success -> {
                    Log.d("Elijah","Got success in network bound resource")

                    saveCallResult(response.data!!)

                    val newDbResults = loadFromDb()
                    result.addSource(newDbResults) { newData ->
                        setValue(Resource.Success(newData))
                        result.removeSource(newDbResults)
                    }
                }

                is ApiResponse.Empty -> {
                    //when it is empty, thats fine for our purposes, so pull from db as success
                    Log.d("Elijah","Got empty in network bound resource")
                    setValue(Resource.Error(message = "No job listings found"))
                }
                //when it is an error, return as error
                is ApiResponse.Error -> {
                    Log.d("Elijah","Got error in network bound resource")

                    val newDbResults = loadFromDb()
                    result.addSource(newDbResults) { newData ->
                        setValue(Resource.Error(newData , response.message ?: "unknown error"))
                        result.removeSource(newDbResults)
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected abstract fun saveCallResult(item: ResultType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(params: CallParameters): LiveData<ApiResponse<ResultType>>


}