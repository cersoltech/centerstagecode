package com.digitalsignage.androidplayer.fileutils

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.digitalsignage.androidplayer.App
import com.digitalsignage.androidplayer.utils.AppLog
import com.digitalsignage.androidplayer.utils.Constants.Companion.FOLDER_NAME
import com.digitalsignage.androidplayer.utils.PreferanceRepository
import java.io.File


class ExternalFileHandling {

    companion object {
        private var changedfileName = ""

        internal var notificationManager: NotificationManager? = null

        val id = "main_channel"
        val name = "Channel Name"
        val description = "Channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        var downloadIdTwo: Int = 0
        private var privifyUri: Uri? = null
        lateinit var downloadedPath: Array<out File>
        private var downloadedFileName: ArrayList<String> = ArrayList()
        private var sameFileName: ArrayList<String> = ArrayList()
        //private var downloadRefIDList = ArrayList<String>()

        internal val URL12 = "https://www.hq.nasa.gov/alsj/a17/A17_FlightPlan.pdf"
        private val folderName = "digital_signage"

        private fun getDownloadsPath(): String {
            return Environment.getExternalStorageDirectory().path + File.separator +Environment.DIRECTORY_DOWNLOADS +File.separator + folderName
            //return Environment.getExternalStorageDirectory().path + File.separator + Environment.DIRECTORY_DOWNLOADS
        }

        fun downloadedPath(context: Context): Array<out File>? {
            val dir: File = App.getInstance().filesDir
            val list = dir.listFiles()
            if (list != null) {
                File(App.getInstance().filesDir, FOLDER_NAME)
                AppLog.d("file downloadedPath", "${list.size}")
                return list
            } else {
                return null
            }
        }

        fun downloadFileFromManager(context: Context, url: String, fileName: String) {
            val directory = File(getDownloadsPath())

            if (!directory.exists())
                directory.mkdirs()

            val downloadManager = App.getInstance().getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle(fileName)
                .setDescription("File is downloading...")
                .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, File.separator+ folderName+File.separator+ fileName
                )
               // .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            //Enqueue the download.The download will start automatically once the download manager is ready
            // to execute it and connectivity is available.
            val downLoadId = downloadManager.enqueue(request)
            var downloadRefIDList = PreferanceRepository.getDownloadRefList("downloadRefSet")?.toMutableList()

            if (downloadRefIDList == null)
            {
                downloadRefIDList = mutableListOf()
            }

            downloadRefIDList.add(downLoadId.toString())
            Log.d("manager", "downloadFileFromManager: ${downloadRefIDList.size}")
            PreferanceRepository.setDownloadRefList(ArrayList(downloadRefIDList))
        }


        /**
         * Method to retrieve Files from storage
         */
        fun getStorageVideoFiles(): ArrayList<File> {
            val downloadedPath = ArrayList<File>()
            val file = File(getDownloadsPath())
            val listOfFiles = file.listFiles()

            if (!listOfFiles.isNullOrEmpty())
                for (f in listOfFiles)
                    if (f.isFile)
                        downloadedPath.add(f)

            return downloadedPath
        }

        /**
         * Method to check if the given File exists
         */
        fun isFileExists(fileName: String): Boolean {
            return getStorageVideoFiles().find { it.name == fileName }?.exists() ?: false
        }
    }//companion obj end
}