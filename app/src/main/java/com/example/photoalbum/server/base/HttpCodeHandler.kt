package com.example.photoalbum.server.base

typealias HttpCodeHandler = Pair<HttpResponseCode, ((errorMapWithList: Map<String, List<String>>, errorMap: Map<String, String>, errorList: List<String>) -> Boolean)>