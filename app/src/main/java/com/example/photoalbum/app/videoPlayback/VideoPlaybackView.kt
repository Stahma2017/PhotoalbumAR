package com.example.photoalbum.app.videoPlayback

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import cn.easyar.Engine
import com.example.photoalbum.R
import com.example.photoalbum.app.base.PmBaseFragment
import com.example.photoalbum.eventbus.BackPressedEvent
import com.example.photoalbum.ext.argument
import com.example.photoalbum.unclassified.navigation.coordinator.CoordinatorHolder
import com.example.photoalbum.unclassified.navigation.coordinator.CoordinatorRouter
import com.example.photoalbum.unrefactored.GLView
import com.example.photoalbum.util.Utils
import kotlinx.android.synthetic.main.view_video_playback.*
import org.greenrobot.eventbus.EventBus
import org.kodein.di.LateInitKodein
import org.kodein.di.generic.instance

import java.io.Serializable

class VideoPlaybackView : PmBaseFragment<VideoPlaybackPm>(){

    override val layoutRes = R.layout.view_video_playback

    private var param: Param by argument("param")

    override fun providePresentationModel() = VideoPlaybackPm(parentCoordinatorEvent)
    private val parentKodein = LateInitKodein()
    private val parentCoordinatorEvent: CoordinatorRouter by parentKodein.instance<CoordinatorHolder>()
    private val eventBus: EventBus by parentKodein.instance()
    private lateinit var glView: GLView

    override fun onCreate(savedInstanceState: Bundle?) {
        parentKodein.baseKodein = getClosestParentKodein()
        super.onCreate(savedInstanceState)
        glView = GLView(context)

    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initCamera()
        }
    }

    override fun onPause() {
        glView.onPause()
        super.onPause()
    }

    override fun prepareUi(view: View, savedInstanceState: Bundle?) {
        super.prepareUi(view, savedInstanceState)
        // to prepare ui
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initCamera()
        } else if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED && shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA)) {
            val alertDialog = Utils.createAlertDialog(activity!!, "Внимание!", "Для работы камеры необходимо разрешение",
                "Настройки", "Отмена", DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", activity!!.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }, DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    eventBus.post(BackPressedEvent())
                    dialogInterface.dismiss()
                })
            alertDialog?.show()
        } else if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED && shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA) === false) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION)
        }
    }

    private fun initCamera() {
        //init camera
        Toast.makeText(context, "Камера такая инициализировалась. Вжууух", Toast.LENGTH_SHORT).show()
        preview.removeAllViews()
        preview.addView(glView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }


    companion object {
        private const val REQUEST_PERMISSION = 100
    }

    class Param : Serializable
}