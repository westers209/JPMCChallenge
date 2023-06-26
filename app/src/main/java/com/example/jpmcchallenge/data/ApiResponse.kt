package com.example.jpmcchallenge.data

data class ApiResponse<out T>(val status: ApiStatus, val data: T?, val message: String?){
    companion object {
        fun<T> success(data: T?): ApiResponse<T> =
            ApiResponse(
                ApiStatus.SUCCESS,
                data,
                null
            )
        fun<T> error(data: T?, message: String?): ApiResponse<T> =
            ApiResponse(
                ApiStatus.ERROR,
                data,
                message
            )
        fun<T> loading(data: T?): ApiResponse<T> =
            ApiResponse(
                ApiStatus.LOADING,
                data,
                null
            )
    }
}

enum class ApiStatus{
    SUCCESS,
    ERROR,
    LOADING
}