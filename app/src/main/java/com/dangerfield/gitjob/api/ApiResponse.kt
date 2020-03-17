package com.dangerfield.gitjob.api

sealed class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : ApiResponse<T>(data)
    class Empty<T>(message: String?, val errorType: GitHubErrorMessage) : ApiResponse<T>(null, message)
    class Error<T>(data: T? = null, message: String) : ApiResponse<T>(data, message)
}