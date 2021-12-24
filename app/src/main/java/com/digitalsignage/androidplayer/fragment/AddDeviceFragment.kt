package com.digitalsignage.androidplayer.fragment

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalsignage.androidplayer.App
import com.digitalsignage.androidplayer.BR
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.base.BaseFragment
import com.digitalsignage.androidplayer.databinding.ActivityAddDeviceBinding
import com.digitalsignage.androidplayer.utils.*
import com.digitalsignage.androidplayer.viewmodel.AddDeviceViewModel
import com.digitalsignage.androidplayer.viewmodel.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class AddDeviceFragment : BaseFragment<ActivityAddDeviceBinding, LoginViewModel>() {
    private val TAG = AddDeviceFragment::class.java.simpleName

    override fun getViewModel(): LoginViewModel = LoginViewModel()

    override fun getFragmentLayout(): Int = R.layout.activity_add_device

    override fun getBindingVariable(): Int = BR.addDeviceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = initBinding(inflater, container)
        setUpLoader(mBinding!!.layoutLoader)
        mBinding!!.imgBack.visibility = View.GONE
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("adddevice", "setupObservers: ${getMacAddress()}")
        Log.d("adddevice", "setupObservers1: ${getMacAddress2("wlan0")}")

        mBinding!!.btnSubmit.setOnClickListener {
           /* if (!isMandatoryCompleted()) {
                return@setOnClickListener
            } else{*/
                //setupObservers()
           // }
            if (allEmpty())
            {
                showShortToast(requireActivity(),"Please enter the values")
            }else
            {
              setupObservers()
            }
        }


    }

    private fun optionalCheck(): Boolean {
        if (mBinding!!.edtDeviceIp.text.toString().trim().isEmpty() ||
            mBinding!!.edtDeviceName.text.toString().trim().isEmpty() ||
            mBinding!!.edtDeviceId.text.toString().trim().isEmpty() ||
            mBinding!!.edtScreenHt.text.toString().trim().isEmpty() ||
            mBinding!!.edtScreenWidth.text.toString().trim().isEmpty())
        {
            return true
        }
        return false
    }

    private fun allEmpty(): Boolean {
         if ( mBinding!!.edtDeviceIp.text.toString().trim().isEmpty() &&
             mBinding!!.edtDeviceName.text.toString().trim().isEmpty() &&
             mBinding!!.edtDeviceId.text.toString().trim().isEmpty() &&
             mBinding!!.edtScreenHt.text.toString().trim().isEmpty() &&
             mBinding!!.edtScreenWidth.text.toString().trim().isEmpty())
         {
             return true
         }
        return false
    }

    private fun setupObservers() {
        val deviceIP = mBinding!!.edtDeviceIp.text.toString().trim()
        val deviceName = mBinding!!.edtDeviceName.text.toString().trim()
        val screenHeight = mBinding!!.edtScreenHt.text.toString().trim()
        val screenWidth = mBinding!!.edtScreenWidth.text.toString().trim()
        val deviceMacAddress = mBinding!!.edtDeviceId.text.toString().trim()

        mViewModel?.getAddDeviceData(
            deviceIP,
            deviceName,
            deviceMacAddress,
            screenHeight,
            screenWidth
        )

        mViewModel!!.addDeviceData.observe(viewLifecycleOwner, {
            when (it) {
                is State.Loading -> {
                    AppLog.i(TAG,"add device loading")
                }

                is State.Error -> {
                    AppLog.i(TAG,"add device error")
                }
                is State.Success -> {
                    AppLog.d(TAG, "Success: ")
                    if (it.code == 200) {
                        if (it.data.status == true) {
                            showLongToast(activity!!, it.data.message)
                            if (it.data.data != null)
                            {
                                mBinding!!.edtDeviceIp.text?.clear()
                                mBinding!!.edtDeviceName.text?.clear()
                                mBinding!!.edtDeviceId.text?.clear()
                                mBinding!!.edtScreenHt.text?.clear()
                                mBinding!!.edtScreenWidth.text?.clear()
                            }
                        } else {
                            showLongToast(activity!!, it.data.message)
                        }
                    }
                }
            }
        })

    }

    private fun isMandatoryCompleted(): Boolean {
        return when {
            mBinding!!.edtDeviceIp.text.toString().trim().isEmpty() -> {
                showShortToast(requireActivity(),"Please Enter Device Name")
                false
            }
            mBinding!!.edtDeviceName.text.toString().trim().isEmpty() -> {
                showShortToast(requireActivity(),"Please Enter Device IP")
                false
            }
            mBinding!!.edtDeviceId.text.toString().trim().isEmpty() -> {
                showShortToast(requireActivity(),"Please Enter Device Mac Address")
                false
            }
            mBinding!!.edtScreenHt.text.toString().trim().isEmpty() -> {
                showShortToast(requireActivity(),"Please Enter Device Screen Height")
                false
            }
            mBinding!!.edtScreenWidth.text.toString().trim().isEmpty() -> {
                showShortToast(requireActivity(),"Please Enter Device Screen width")
                false
            }
            else -> true
        }
    }
}