package com.example.photoalbum.app.splash

import android.os.Handler
import com.example.photoalbum.Screens
import com.example.photoalbum.app.base.BasePresentationModel
import ru.terrakok.cicerone.Router

class SplashPm(val router: Router, private val pushData: String?) : BasePresentationModel() {

    override fun onBind() {
       Handler().postDelayed({
           router.replaceScreen(Screens.Root(pushData))
       }, 1500L)

    }
}