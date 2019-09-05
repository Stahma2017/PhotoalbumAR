package com.example.photoalbum.server.base

import com.example.photoalbum.server.base.RequestResult

interface RequestErrorHandler {
    fun processError(throwable: RequestResult.Error)
}