package com.dangerfield.gitjob.api

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

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

    fun build(params: CallParameters): NetworkBoundResource<ResultType, CallParameters> {
        result.value = Resource.Loading(null)
        val dbSource = loadFromDb()
        @Suppress("LeakingThis")
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource, params)
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(Resource.Success(newData))
                    result.removeSource(dbSource)
                }
            }
        }

        return this
    }

    fun refresh(params: CallParameters): NetworkBoundResource<ResultType, CallParameters> {
        val apiResponse = createCall(params)

        setValue(Resource.Loading())
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)


            when(response) {

                is ApiResponse.Success -> {
                    saveCallResult(response.data!!)
                }

                is ApiResponse.Empty -> {
                    setValue(Resource.Error(message = response.message!!))
                    val dbSource = loadFromDb()

                    result.addSource(dbSource) { newData ->
                        setValue(Resource.Error(data = newData, message = response.message, errorType = response.errorType))
                        result.removeSource(dbSource)
                    }
                }

                is ApiResponse.Error -> {
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

    // shows data from DB while api call loads. Upon success saves to DB to keep single source of truth
    // leaves 1 DB source for the life of the app
    private fun fetchFromNetwork(dbSource: LiveData<ResultType>, params: CallParameters) {
        val apiResponse = createCall(params)

        result.addSource(dbSource) { newData -> setValue(Resource.Loading(newData)) }


        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)

            when(response) {

                is ApiResponse.Success -> {
                    saveCallResult(response.data!!)
                    val newDbResults = loadFromDb()
                    result.addSource(newDbResults) { newData ->
                        setValue(Resource.Success(newData))
                    }
                }

                is ApiResponse.Empty -> {
                    setValue(Resource.Error(message = response.message!!))
                    val newDbResults = loadFromDb()
                    result.addSource(newDbResults) { newData ->
                        setValue(Resource.Error(data = newData, message = response.message, errorType = response.errorType))
                    }
                }

                is ApiResponse.Error -> {
                    val newDbResults = loadFromDb()
                    result.addSource(newDbResults) { newData ->
                        setValue(Resource.Error(newData , response.message ?: "unknown error"))
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