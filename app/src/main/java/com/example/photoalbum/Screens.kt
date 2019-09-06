package com.example.photoalbum

import android.content.Context
import android.content.Intent
import com.example.photoalbum.app.root.RootView
import com.example.photoalbum.app.videoPlayback.VideoPlaybackView
import com.example.photoalbum.ext.createFragment
import com.example.photoalbum.notification.PushObject
import org.jetbrains.anko.intentFor
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    data class Root(val pushData: String?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context): Intent {
            val intentInner = context.intentFor<RootView>()
            pushData?.let {
                intentInner.putExtra(PushObject.PUSH_DATA, it)
            }
            return intentInner
        }
    }

    object VideoPlayback : SupportAppScreen() {
        override fun getFragment() = createFragment<VideoPlaybackView, VideoPlaybackView.Param>(
            VideoPlaybackView.Param()
        )
    }

    data class WebBrowser(val site: String) : SupportAppScreen()
}