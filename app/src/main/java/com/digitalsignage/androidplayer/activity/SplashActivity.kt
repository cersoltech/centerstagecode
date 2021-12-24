package com.digitalsignage.androidplayer.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.digitalsignage.androidplayer.BR
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.base.BaseActivity
import com.digitalsignage.androidplayer.databinding.ActivitySplashBinding
import com.digitalsignage.androidplayer.fileutils.FileUtility.Companion.decryptText
import com.digitalsignage.androidplayer.fileutils.FileUtility.Companion.encryptText
import com.digitalsignage.androidplayer.utils.*
import com.digitalsignage.androidplayer.viewmodel.SplashViewModel
import kotlinx.coroutines.*


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(){
    private var serial: String?= null
    private val TAG = SplashActivity::class.java.simpleName
    private val SPLASH_TIME_OUT = 1000L
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun getViewModel(): SplashViewModel = SplashViewModel()

    override fun getContentLayout(): Int = R.layout.activity_splash
    override fun getBindingVariable(): Int = BR.splashViewModel

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

        val resText = "16-08-2021_d60502168b90dc7c"
        val res = "ABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890"
        Log.d(TAG, "onCreate: encryppt - ${encryptText(resText)}")
        Log.d(TAG, "onCreate: decrypt - ${
            encryptText(resText).let {
                decryptText(it)
                        .replace("<", "2").replace(">", "4")
            }
        }")

        if (PreferanceRepository.getDownloadRefList("downloadRefSet") != null)
        {
            PreferanceRepository.setDownloadRefList(arrayListOf())
        }

        PreferanceRepository.setInt(Constants.Current_Device_Layout_ID, 0)

       activityScope.launch {
           delay(SPLASH_TIME_OUT)
            if (PreferanceRepository.getBolean(Constants.isLicenseActive))
            {
                startActivity(Intent(this@SplashActivity, DeviceInfoActivity::class.java))
            }else
            {
                startActivity(Intent(this@SplashActivity, LicensingActivity::class.java))
            }
           finish()
       }

        mViewModel!!.getTemplatesData.observe(this, {
            when (it) {
                is State.Success -> {
                    AppLog.d(tag = TAG, "Success: ")
                    if (it.code == 200) {
                        if (it.data.status == true) {
                            if (it.data.data != null && it.data.data!!.isNotEmpty()) {
                                val data = it.data.data
                                if (data?.size!! > 0) {
                                    //saved in sharedpref
                                    saveAllTemplateDesign(data = it.data)
                                }
                            } else {
                                AppLog.d(TAG, "templates data is empty")
                            }
                        }
                    }
                }
            }
        })
    }

    fun checkPermission() {
        //Log.Info(TAG, "Checking permission.")

        // Check if the  permission is already available.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            //  permission has not been granted
            //RequestPhoneStatePermission()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 115)
        } else {
            //  permissions is already available
            //Log.Info(TAG, " permission has already been granted.")
           getInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 115) {
            // Received permission result for camera permission.
            //Log.Info(TAG, "Received response for phone state permission request.");

            // Check if the only required permission has been granted
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                //Log.Info(TAG, "phonestate permission has now been granted. Showing preview.");
                //Snackbar.Make(layout, Resource.String.permission_available_phonestate, Snackbar.LengthShort).Show();

                getInfo()

            }
        }
    }
        @SuppressLint("HardwareIds")
        private fun getInfo() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                Log.i("TAG", "SERIAL: " + Build.getSerial());
                serial = Build.getSerial()
            }else
            {
                serial = Build.SERIAL
                Log.i("TAG", "SERIAL: " + Build.SERIAL);
            }
        }

    fun encrypt(str: String): String? {
        var code: Int
        var result = ""
        for (element in str) {
            code = Math.round(Math.random().toFloat() * 8 + 1)
            result += code.toString() + Integer.toHexString(element.toInt() xor code) + "-"
        }
        return result.substring(0, result.lastIndexOf("-"))
    }

    fun decrypt(str: String): String? {
        var str = str
        str = str.replace("-", "")
        var result = ""
        var i = 0
        while (i < str.length) {
            val hex = str.substring(i + 1, i + 3)
            result += (hex.toInt(16) xor str[i].toString().toInt()).toChar()
            i += 3
        }
        return result
    }

    override fun onStart() {
        super.onStart()
    }
    override fun onPause() {
        super.onPause()
        //activityScope.cancel()
    }


    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }

}


