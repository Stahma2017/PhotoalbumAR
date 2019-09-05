package com.example.photoalbum.kodeininjectionmanager

import org.kodein.di.Kodein

interface IHasRetainedKodein {
    fun getRetainedKodein(): Kodein
    fun getKodeinKey(): String
}