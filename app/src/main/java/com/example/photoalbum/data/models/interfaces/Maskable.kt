package com.example.photoalbum.data.models.interfaces

interface Maskable {
    fun getRawText(): String
    fun getMaskedText(): String
}