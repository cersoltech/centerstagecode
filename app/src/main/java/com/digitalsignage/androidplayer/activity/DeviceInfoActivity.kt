package com.digitalsignage.androidplayer.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.digitalsignage.androidplayer.BR
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.base.BaseActivity
import com.digitalsignage.androidplayer.databinding.ActivityDeviceInfoBinding
import com.digitalsignage.androidplayer.fileutils.FileUtility.Companion.getDeviceId
import com.digitalsignage.androidplayer.fragment.AddDeviceFragment
import com.digitalsignage.androidplayer.fragment.AppSettingFragment
import com.digitalsignage.androidplayer.fragment.DeviceSelectionFragment
import com.digitalsignage.androidplayer.utils.*
import com.digitalsignage.androidplayer.viewmodel.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class DeviceInfoActivity : BaseActivity<ActivityDeviceInfoBinding, LoginViewModel>() {

    private val TAG = DeviceInfoActivity::class.java.simpleName
    private lateinit var appSettingsFragment: AppSettingFragment
    private lateinit var addDeviceFragment: AddDeviceFragment
    private lateinit var deviceSelectionFragment: DeviceSelectionFragment

    override fun getViewModel(): LoginViewModel = LoginViewModel()

    override fun getContentLayout(): Int = R.layout.activity_device_info

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_device_info)
        initBinding()
        mBinding!!.bottomNavigationView.setPadding(0, 0, 0, 0)
        removePaddingFromNavigationItem()

         deviceSelectionFragment=DeviceSelectionFragment()
         addDeviceFragment=AddDeviceFragment()
         appSettingsFragment=AppSettingFragment()

        setCurrentFragment(deviceSelectionFragment)

        if (PreferanceRepository.getBolean(Constants.islicenseOnlineRegister) != null)
        {
            val isLicenseOnlineRegister = PreferanceRepository.getBolean(Constants.islicenseOnlineRegister)
              if (isLicenseOnlineRegister)
              {
                  setupObservers()
              }else
              {
                  if (PreferanceRepository.getString(Constants.expiryDate) != "")
                  {
                      val isDateExpired = isLicenseExpired(PreferanceRepository.getString(Constants.expiryDate))
                      if (isDateExpired)
                      {
                          PreferanceRepository.setBoolean(Constants.isLicenseActive,false)
                          showLicencingDialog()
                      }
                  }
              }
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onStart() {
        super.onStart()

        mBinding?.bottomNavigationView?.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.device_selection -> {
                    deviceSelectionFragment.isUserClickedOnUI = true
                    setCurrentFragment(deviceSelectionFragment)
                }
                R.id.add_device -> {
                    deviceSelectionFragment.isUserClickedOnUI = true
                    setCurrentFragment(addDeviceFragment)
                }
                R.id.app_settings -> {
                    deviceSelectionFragment.isUserClickedOnUI = true
                    setCurrentFragment(appSettingsFragment)
                }
            }
            true
        }
    }

    private fun setupObservers() {
        getDeviceId(this)?.let { mViewModel?.getLicenseApiData(it) }

        mViewModel!!.getLicenseData.observe(this, {
            when (it) {
                is State.Success -> {
                    AppLog.d(tag = TAG, "Success: ")
                    if (it.code == 200) {
                        if (it.data.status) {
                            if (it.data.data != null) {
                                if (it.data.message != null)
                                {
                                  Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else
                        {
                            PreferanceRepository.setBoolean(Constants.isLicenseActive,false)
                            showLicencingDialog()
                            //Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    fun removePaddingFromNavigationItem() {
        val menuView = mBinding!!.bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView
            val activeLabel = item.findViewById<View>(R.id.largeLabel)
            (activeLabel as? TextView)?.setPadding(0, 0, 0, 0)
            //setMargin((activeLabel as? TextView)!!,0,0,5,0)
        }
    }

    fun setMargin(view: TextView, left: Int, right: Int, top: Int, bottom: Int) {
        val params = view.layoutParams as MarginLayoutParams
        params.setMargins(left, top, right, bottom)
        view.layoutParams = params
    }

    private fun setCurrentFragment(currentFrag: Fragment){
        fragmentTransaction(
                REPLACE_FRAGMENT,
                currentFrag,
                R.id.flFragment,
                true,
                null
        )
    }

    private fun showLicencingDialog(){
        val builder = AlertDialog.Builder(this)

        builder.setTitle(resources.getString(R.string.licencing))
        builder.setMessage(resources.getString(R.string.license_msg))
        builder.setCancelable(false)
        builder.setPositiveButton("UPDATE") { dialog, which ->
            startActivity(Intent(this,LicensingActivity::class.java))
            finish()
            dialog.dismiss()
        }
        builder.setNegativeButton("CANCEL") { dialog, which ->
            //dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}