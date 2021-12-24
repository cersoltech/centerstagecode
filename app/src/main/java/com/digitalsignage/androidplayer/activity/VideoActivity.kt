package com.digitalsignage.androidplayer.activity

import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.fileutils.ExternalFileHandling
import com.digitalsignage.androidplayer.utils.getFileExtension
import kotlinx.android.synthetic.main.activity_video.*
import java.io.File


class VideoActivity : AppCompatActivity() {

    private val videoUrl = "http://devdss.swantech.ae/uploads/videos/88475_1610972260.mp4"
    private val videoUrl1 = "http://techslides.com/demos/sample-videos/small.mp4"

    lateinit var uri: Uri
    lateinit var videouri: Uri
    private var videoFiles = ArrayList<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        videoFiles = ExternalFileHandling.getStorageVideoFiles()

        //Creating MediaController
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoview_player)
        //specify the location of media file
               /* val uri: Uri = Uri.parse(
                    Environment.getExternalStorageDirectory().getPath() + "/media/1.mp4"
                );*/


        if (videoFiles.isNotEmpty()) {
            videoFiles.forEach { file ->
                val extension = getFileExtension(file)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    uri = FileProvider.getUriForFile(
                        this,
                        "${this.packageName}.provider", file
                    )
                    //videouri = Uri.parse("content://com.digitalsignage.androidplayer.provider/files/androidplayer/48_1_18_35551_1611578015.mp4")
                    videouri = Uri.parse("content://com.digitalsignage.androidplayer.provider/%2F/Download/digital_signage/54_1_1_1_1_1_4_17_39500_1618208517.mp4")
                   }
                }
            }
        //Setting MediaController and URI, then starting the videoView
        videoview_player.setMediaController(mediaController)
        //mVideoView.setVideoPath("file:///android_asset/video1.MP4");
        //videoview_player.setVideoPath(Uri.parse(videoUrl).toString())
        //videoview_player.setVideoURI(Uri.parse(videoUrl1))
        videoview_player.setVideoURI(videouri)
        videoview_player.setOnPreparedListener(OnPreparedListener { mp ->
            mp.isLooping = true
            videoview_player.start()
        })
        //videoview_player.requestFocus()
        //videoview_player.start()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}