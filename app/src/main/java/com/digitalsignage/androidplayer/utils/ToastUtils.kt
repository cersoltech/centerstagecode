package com.digitalsignage.androidplayer.utils

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.digitalsignage.androidplayer.fileutils.ExternalFileHandling
import java.io.File
import java.net.NetworkInterface
import java.net.SocketException
import java.text.SimpleDateFormat
import java.util.*


enum class LOADER_STATE { LOADING, SUCCESS, ERROR }

fun showShortToast(context: Context, message: String?){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showLongToast(context: Context, message: String?){
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun getCurrentdate(format: String = "dd-MM-yyyy"): String {
    val c = Calendar.getInstance().time
    println("Current time => $c")
    val df = SimpleDateFormat(format, Locale.getDefault())
    return df.format(c)
}


/**
 * Method to get Calendar Instance wherever required
 */
fun getCalendarInstance(): Calendar = Calendar.getInstance(Locale.getDefault())

fun getCurrentDateString(addNoOfDays: Int = 0): String =
        SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault()).format(
                Calendar.getInstance().apply { add(Calendar.DATE, addNoOfDays) }.time
        )


//check current time is between start time & end time
fun isCurrentTimeBetweenStartAndEndTime(startTime: Calendar, endTime: Calendar): Boolean{
    val currentTime = getCurrentDateString()
    Log.d("timebetween", "isCurrentTimeBetweenStartAndEndTime: $currentTime")
    Log.d("timebetween", "isCurrentTimeBetweenStartAndEndTime: $startTime/ $endTime")
    AppLog.d(
            tag = "timebetween",
            "value: ${getCalendarInstance().timeInMillis in startTime.timeInMillis until endTime.timeInMillis}"
    )
    //return getCalendarInstance().timeInMillis in startTime.timeInMillis until endTime.timeInMillis
    //return parseDateTimeToCalendar(currentTime).timeInMillis in startTime.timeInMillis until endTime.timeInMillis
    return parseDateTimeToCalendar(currentTime).after(startTime) && parseDateTimeToCalendar(
            currentTime
    ).before(endTime)
}

/**
 * This method will take a string & return time accordingly
 * @param dateTimeString Takes a string in format "yyyy-MM-dd HH:mm:ss"
 * @return returns an instance of [Calendar] with updated time
 */
fun parseDateTimeToCalendar(dateTimeString: String): Calendar =
    getCalendarInstance().apply {
        time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeString)
    }


fun checkCurrentTime(startTime: String, endTime: String): Boolean{
    val sdf = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault())
    val startDate = sdf.parse(startTime)
    val endDate = sdf.parse(endTime)

    val currentDateTime = Date()
    Log.d("timebetween", "checkCurrentTime: ${sdf.format(currentDateTime)}")
    return currentDateTime.after(startDate) && currentDateTime.before(endDate)
}

fun checkIfStartTimeAfterCurrentTime(startTime: String): Boolean{
    val sdf = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault())
    val startDate = sdf.parse(startTime)

    val currentDateTime = Date()
    Log.d("timebetween", "checkIfStartTimeAfterCurrentTime: ${sdf.format(currentDateTime)}")
    return currentDateTime.before(startDate)
}

fun isLicenseExpired(expiryDate: String): Boolean{
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val endDate = sdf.parse(expiryDate)

    val currentDateTime = Date()
    Log.d("expiryDateChk", "checkIfEndTime: ${sdf.format(currentDateTime)}")
    return currentDateTime.after(endDate)
}



 fun checkIfFileAlreadyPresent(context: Context, fileName: String): Boolean{

     var fileFound: Boolean = false
     var downloadedFiles = ArrayList<File>()
     val downloadedFilesNameList = ArrayList<String>()
     var downloadedFileName = ""

    downloadedFilesNameList.clear()
    downloadedFiles.clear()
    downloadedFiles = ExternalFileHandling.getStorageVideoFiles()

    if (downloadedFiles.isNotEmpty())
    {
        downloadedFiles.forEach { fileUrl ->
            downloadedFileName = getFileName(fileUrl.path)
            //Log.d("files", "checkIfFileAlreadyPresent: downloadedFileName $downloadedFileName")
            downloadedFilesNameList.add(downloadedFileName)
            //fileUrl.delete()
        }
    }
     Log.d("files", "checkIfFileAlreadyPresent size ${downloadedFilesNameList.size}")
     Log.d("files", "checkIfFileAlreadyPresent: fileName $fileName")


     if (downloadedFilesNameList.isNotEmpty())
    {
        downloadedFilesNameList.forEach {
            if (fileName.equals(it))
            {
                Log.d("files", "checkIfFileAlreadyPresent: $fileName alreday downloaded")
                fileFound = true
            }else
            {
                Log.d("files", "checkIfFileAlreadyPresent: $fileName not downloaded")
                fileFound =  false
            }
        }
        /*if (downloadedFilesNameList.contains(fileName))
        {
            Log.d("files", "checkIfFileAlreadyPresent: $fileName alreday downloaded")
            return true
        }else
        {
            Log.d("files", "checkIfFileAlreadyPresent: $fileName not downloaded")
            return false
        }*/
    }
    return fileFound
}

fun getMacAddress(): String {
    try {
        val all: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (networkInterface in all) {
            if (!networkInterface.getName().equals("wlan0", true)) continue
            val macBytes: ByteArray = networkInterface.getHardwareAddress() ?: return ""
            val res1 = StringBuilder()
            Log.e("get mac", "getMacAddr: $res1")
            for (b in macBytes) {
                // res1.append(Integer.toHexString(b & 0xFF) + ":");
                res1.append(String.format("%02X:", b))
            }
            if (res1.isNotEmpty()) {
                res1.deleteCharAt(res1.length - 1)
            }
            return res1.toString().replace(":", "-")
        }
    } catch (ex: Exception) {
        Log.e("TAG", "getMacAddr: ", ex)
    }
    return ""
}

 fun getMacAddress2(interfaceName: String): String? {
    return try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            if (TextUtils.equals(networkInterface.name, interfaceName)) {
                val bytes = networkInterface.hardwareAddress
                val builder = java.lang.StringBuilder()
                for (b in bytes) {
                    builder.append(String.format("%02X:", b))
                }
                if (builder.length > 0) {
                    builder.deleteCharAt(builder.length - 1)
                }
                return builder.toString()
            }
        }
        "<EMPTY>"
    } catch (e: SocketException) {
        Log.e("", "Get Mac Address Error", e)
        "<ERROR>"
    }
}



