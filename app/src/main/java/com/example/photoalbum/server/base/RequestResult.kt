package com.example.photoalbum.server.base

sealed class RequestResult<out T> {
    data class Success<out T>(val data: T) : RequestResult<T>()

    sealed class Error : RequestResult<Nothing>() {
        data class HttpCode400(
            val errorMapWithList: Map<String, List<String>>,
            val errorMap: Map<String, String>,
            val errorList: List<String>
        ) : Error()

        data class HttpCode404(
            val errorMapWithList: Map<String, List<String>>,
            val errorMap: Map<String, String>,
            val errorList: List<String>
        ) : Error()

        object HttpCode401 : Error()
        object HttpCodeAnother : Error()

        object UnknownHost : Error()
        object SocketTimeout : Error()

        data class Another(val throwable: Throwable) : Error()
    }
}