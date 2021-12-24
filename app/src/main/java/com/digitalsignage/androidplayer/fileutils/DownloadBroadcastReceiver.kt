package com.digitalsignage.androidplayer

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.digitalsignage.androidplayer.utils.Constants
import com.digitalsignage.androidplayer.utils.PreferanceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.io.File

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class DownloadBroadcastReceiver : BroadcastReceiver() {
    val TAG = "DownloadBroadcastRec"
    private lateinit var downloadManager: DownloadManager

    companion object{
        private var listener: OnDownloadListener? = null

        fun setDownloadListener(listener: OnDownloadListener?){
            this.listener = listener
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action
        downloadManager = App.getInstance().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            //remove ref id
            val downloadRefList = PreferanceRepository.getDownloadRefList("downloadRefSet")?.toMutableList()
            Log.d(TAG, "onReceive:before ${downloadRefList?.size}")
           /* val id = PreferanceRepository.getLong(downloadId.toString())
            Log.e(TAG, "id::::::: ${id}")*/
            downloadRefList?.remove("$downloadId")

            if (downloadRefList?.size != 0)
            {
                PreferanceRepository.setDownloadRefList(ArrayList(downloadRefList))
            }else
            {
                Log.d(TAG, "onReceive: complete1")
                //if (PreferanceRepository.getBolean(Constants.Lanuch_MainScreen))
                //{
                   listener?.onDownloadComplete()
                //}
                if (PreferanceRepository.getDownloadRefList("downloadRefSet") != null)
                {
                    PreferanceRepository.setDownloadRefList(arrayListOf())
                }
                //mBinding!!.layoutLoader.pbLoader.visibility =View.GONE
                //FileUtility.hideProgress()
            }

            val query = DownloadManager.Query()
            query.setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            cursor?.let {
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                        var localFile = cursor.getString(
                            cursor.getColumnIndex(
                                DownloadManager.COLUMN_LOCAL_URI
                            )
                        )

                        Log.e("fullFileName", localFile.toString())

                        if (localFile.contains("file:///")) {
                            localFile = localFile.removePrefix("file:///").substringBeforeLast(
                                File.separator
                            )
                        }
                        //Toast.makeText(context, localFile, Toast.LENGTH_LONG).show()
                        Log.e("localFileName", localFile.toString())
                    } else if (DownloadManager.STATUS_FAILED == cursor.getInt(columnIndex)) {
                        //Toast.makeText(context, "Some Error", Toast.LENGTH_LONG).show()

                        Log.e("localFileName", "STATUS_FAILED")
                    }
                }

                /*  val bytes_downloaded =
                      cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                  val bytes_total =
                      cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                  val dl_progress =
                      if (bytes_total > 0) (bytes_downloaded * 100L / bytes_total).toInt() else 0*/
                //final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                /*  activity!!.runOnUiThread {
                      mBinding!!.layoutLoader.pbLoader.setProgress(dl_progress)
                  }*/
                cursor.close()
            }
        }
    }

}

interface OnDownloadListener{
    fun onDownloadComplete()
}