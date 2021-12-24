package com.digitalsignage.androidplayer.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.databinding.PasswordDialogBinding
import com.digitalsignage.androidplayer.fileutils.ExternalFileHandling
import com.digitalsignage.androidplayer.fileutils.FileUtility
import com.digitalsignage.androidplayer.model.response.AssetDownloadScheduleResponse
import com.digitalsignage.androidplayer.model.response.GetTemplatesResponse
import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

interface CustDialogListerner {
    fun onFilerValuesSet(
        templatedId: Int, headerTxtcolorCode: String, headerBgColor: String,
        headerFont: String, headerFontSize: Int, headerTxtAlign: String,
        headerFontStye: String, headerLableType: String,
        queueTxtcolorCode: String, queueBgColor: String,
        queueFont: String, queueFontSize: Int, queueTxtAlign: String,
        queueFontStye: String,
        rowsCount: Int, columnsCount: Int,
        tickerTxtcolor: String, tickerBgColor: String,
        tickerFont: String, tickerFontSize: Int, tickerDirection: String,
        tickerFontStye: String, tickerSpeed: String, queueBlinkSpeed: String,
        queueSoundSpeed: String, sound: String, headerReset: Boolean, queueReset: Boolean,
        tickerReset: Boolean, queueLineColor: String,
        rtQueueTxtcolor: String, rtQueueBgColor: String,
        rtQueueFont: String, rtQueueFontSize: Int, rtQueueAlign: String,
        rtQueueFontStyle: String
    )

}

interface SettingDialogListener {
    fun showSettingsDialog()
}


fun getLayout(layoutId: Int): Int {
    when (layoutId) {
        1 -> return R.layout.template_one
        2 -> return R.layout.template_two
        3 -> return R.layout.template_three
        4 -> return R.layout.template_four
        5 -> return R.layout.template_five
        6 -> return R.layout.template_six
        7 -> return R.layout.template_seven
        8 -> return R.layout.template_eight
        9 -> return R.layout.template_nine
        10 -> return R.layout.template_ten
        11 -> return R.layout.template_eleven
        12 -> return R.layout.template_twelve
        13 -> return R.layout.template_thirteen
        14 -> return R.layout.template_fourteen
        15 -> return R.layout.template_fifteen
        16 -> return R.layout.template_sixteen
        17 -> return R.layout.template_seventeen
        18 -> return R.layout.template_eighteen
        19 -> return R.layout.template_ninteen
        20 -> return R.layout.template_twenty
        21 -> return R.layout.template_twentyone
        22 -> return R.layout.twenty_two
        23 -> return R.layout.template_twenty_three
        24 -> return R.layout.template_twenty_four
        25 -> return R.layout.template_twenty_five
        26 -> return R.layout.template_twenty_six
        27 -> return R.layout.template_twenty_seven
        28 -> return R.layout.template_twenty_eight
        29 -> return R.layout.template_twenty_nine
        30 -> return R.layout.template_thirty
        31 -> return R.layout.template_thirty_one
        32 -> return R.layout.template_thirty_two
        33 -> return R.layout.template_thirty_three
        34 -> return R.layout.template_twenty_ninecopy
        35 -> return R.layout.template_twenty_ninesildercopy

        else -> return 0
    }
}

fun getSound(soundName: String): Int {
    return when (soundName) {
        "Ting Tong" -> R.raw.tingtong
        "Blip Blip" -> R.raw.blipblip
        "Knock Knock" -> R.raw.knock
        "Ding Dong" -> R.raw.dingdong
        "Single Beep" -> R.raw.singlebeep
        "Beep" -> R.raw.beep
        else -> R.raw.tingtong
    }
}

fun getSoundTime(sound: String): Int {
    return when (sound) {
        "1" -> 1000
        "2" -> 2000
        "3" -> 3000
        "4" -> 4000
        "5" -> 5000
        "6" -> 6000
        "7" -> 7000
        "8" -> 8000
        "9" -> 9000
        "10" -> 10000
        else -> 2000
    }
}

fun showPasswordDialog(
    context: Context,
    layoutInflater: LayoutInflater, listener: SettingDialogListener
) {
    val designDialog = Dialog(context)

    val designDialogBinding = PasswordDialogBinding.inflate(layoutInflater, null, false)

    designDialog.setContentView(designDialogBinding.root)
    designDialog.setCancelable(true)
    designDialog.setCanceledOnTouchOutside(true)
    designDialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    designDialogBinding.btnPwdsubmit.setOnClickListener {
        //if password match show setting dialog
        val pwd = designDialogBinding.edtPwd.text.toString().trim()
        if (pwd == "1234") {
            listener.showSettingsDialog()
            designDialog.dismiss()
        } else {
            Toast.makeText(context, "Wrong Password", Toast.LENGTH_SHORT).show()
        }
    }
    designDialog.show()
}

fun getTemplateAssetStoredResponse(): AssetDownloadScheduleResponse? {
    val loginData = PreferanceRepository.getString(Constants.template_asset_offline_response)
    return Gson().fromJson(loginData, AssetDownloadScheduleResponse::class.java)
}

fun saveTemplateAssetOfflineData(data: AssetDownloadScheduleResponse) {
    val templateAssetDataToPref = Gson().toJson(data)
    PreferanceRepository.setString(
        Constants.template_asset_offline_response,
        templateAssetDataToPref
    )
}

fun getAllTemplateDesignResponse(): GetTemplatesResponse? {
    val templateData = PreferanceRepository.getString(Constants.template_ui_offline_response)
    return Gson().fromJson(templateData, GetTemplatesResponse::class.java)
}

fun saveAllTemplateDesign(data: GetTemplatesResponse) {
    val templateUiDataToPref = Gson().toJson(data)
    PreferanceRepository.setString(Constants.template_ui_offline_response, templateUiDataToPref)
}


fun getFileExtension(file: File): String {
    val fileName = file.name
    return if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        fileName.substring(fileName.lastIndexOf("."))
    else
        ""
}

fun getFileName(url: String): String {
    val fileName: String = url.substring(url.lastIndexOf('/') + 1, url.length)

    val fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'))

    //Log.d("fileutils", "Real Path: $url")
    Log.d("fileutils", "Filename With Extension: $fileName")
    //Log.d("fileutils", "File Without Extension: $fileNameWithoutExtn")
    return fileName
}

fun getFileNameWithoutExtn(url: String): String {
    val fileName: String = url.substring(url.lastIndexOf('/') + 1, url.length)

    val fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'))

    //Log.d("fileutils", "Real Path: $url")
    //Log.d("fileutils", "Filename With Extension: $fileName")
    //Log.d("fileutils", "File Without Extension: $fileNameWithoutExtn")
    return fileNameWithoutExtn
}

fun checkIfDataIsDownloadedOrNot(context: Context): ArrayList<File> {
    val downloadedPath = ArrayList<File>()
    val downloadedPathString: ArrayList<String> = ArrayList()
    val downloadedFullPath: ArrayList<String> = ArrayList()
    val isDownloadActivity = true

    if (ExternalFileHandling.downloadedPath(context) == null) {
        AppLog.d("file", "No file available")
        return downloadedPath
    } else {
        val list = /*ExternalFileHandling.downloadedPath()!!*/
            File(context.filesDir.path).listFiles()!!
        for (f in list) {
            if (!f.isDirectory)
                downloadedPath.add(f)
        }

        if (downloadedPath.isEmpty()) {
            AppLog.d("file", "No file available")
            return downloadedPath
        }

        downloadedPathString.clear()
        downloadedFullPath.clear()
        for (i in downloadedPath) {
            AppLog.d("file", "FileName: ${i.name}")
            AppLog.d("file", "file absolutePath: ${i.absolutePath}")
            downloadedPathString.add(i.name)
            downloadedFullPath.add(i.absolutePath)
        }
    }
    return downloadedPath
}

/**
 * Method to load images from [Glide]
 */
fun loadImage(context: Context, imageName: String, imageView: ImageView) {
    Glide.with(context)
        .load(if (imageName.isEmpty()) R.drawable.default_placeholder else "${Constants.Video_Base_URL}${imageName}")
        //.error(R.drawable.default_placeholder)
        //.placeholder(R.drawable.default_placeholder)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(imageView)
    AppLog.i("Image_url", "${Constants.Video_Base_URL}${imageName}")
}

fun loadImageFromUri(context: Context, imageName: Uri, imageView: ImageView?) {
    Glide.with(context)
        .load(
            if (imageName.toString().isEmpty()) R.drawable.default_placeholder else "${imageName}"
        )
        //.error(R.drawable.default_placeholder)
        //.placeholder(R.drawable.default_placeholder)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(imageView!!)
    AppLog.i("Image_url loading glide image", "${imageName}")
}

fun MarqueeNoFocus.setMovingText(context: Activity, text: String) = this.post {
    /* val textWidth = this.paint.measureText(text)
     val spaceWidth = this.paint.measureText(" ")
     val requiredAdditionalSpace = ((this.width - textWidth) / spaceWidth).toInt()
     this.text = StringBuilder(text).apply {
       for (i in 0..requiredAdditionalSpace) {
         append(" ")
       }
     }*/
    val data_save = "SAVED_THEME"
    this.init(context.windowManager)
    if (PreferanceRepository.getString(data_save + "_Spinner_ticker_direction").isNotEmpty()) {
        FileUtility.setTickerDirection(
            this,
            PreferanceRepository.getString(data_save + "_Spinner_ticker_direction")
        )
    } else {
        this.setScrollDirection(MarqueeNoFocus.RIGHT_TO_LEFT)
    }

    if (PreferanceRepository.getString(data_save + "_Ticker_speed").isNotEmpty()) {
        FileUtility.setTickerSpeed(
            this,
            PreferanceRepository.getString(data_save + "_Ticker_speed")
        )
    } else {
        this.setScrollMode(MarqueeNoFocus.SCROLL_NORM)
    }

    this.isSelected = true
    /*this.onFocusChangeListener = object : View.OnFocusChangeListener {
      override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
          this@setMovingText.isSelected = true
        }
      }
    }*/
}

fun hasPermissions(context: Context): Boolean {
    var res: Int
    //string array of permissions,
    val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    for (perms in permissions) {
        res = context.checkCallingOrSelfPermission(perms)
        if (res != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}










