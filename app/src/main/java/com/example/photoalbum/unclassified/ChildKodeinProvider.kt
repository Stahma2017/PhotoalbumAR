package com.example.photoalbum.unclassified

import org.kodein.di.Kodein

interface ChildKodeinProvider {
    fun getChildKodein(): Kodein
}