package com.digitalsignage.androidplayer.fragment

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.SharedMemory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalsignage.androidplayer.BR
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.activity.ClientCommunicationActivity
import com.digitalsignage.androidplayer.base.BaseFragment
import com.digitalsignage.androidplayer.databinding.ActivityAppSettingBinding
import com.digitalsignage.androidplayer.fileutils.UdpBroadcastReceiver
import com.digitalsignage.androidplayer.utils.Constants
import com.digitalsignage.androidplayer.utils.PreferanceRepository
import com.digitalsignage.androidplayer.viewmodel.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class AppSettingFragment : BaseFragment<ActivityAppSettingBinding, LoginViewModel>() {

    private lateinit var mContext: Context
    //private lateinit var udpSocketReceiver: UdpBroadcastReceiver

    override fun getViewModel(): LoginViewModel = LoginViewModel()

    override fun getFragmentLayout(): Int = R.layout.activity_app_setting

    override fun getBindingVariable(): Int = BR.loginViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = initBinding(inflater, container)
        mBinding!!.imgBack.visibility = View.GONE

       /* udpSocketReceiver = UdpBroadcastReceiver()
        val intentFilter = IntentFilter("MESSAGE_RECEIVED")
        mContext.registerReceiver(udpSocketReceiver, intentFilter)*/

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding!!.btnSubmit.setOnClickListener {
           //mViewModel?.getUdpFromViewmodel(requireActivity())
          //startActivity(Intent(activity!!,ClientCommunicationActivity::class.java))
            val ipAdds = mBinding!!.edtDeviceip.text.toString().trim()
            val port = mBinding!!.edtPortno.text.toString().trim()
            if (ipAdds.isNotEmpty() && port.isNotEmpty())
            {
               PreferanceRepository.setString(Constants.deviceip,ipAdds)
               PreferanceRepository.setString(Constants.portno,port)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        /*if (udpSocketReceiver != null)
        {
            mContext.unregisterReceiver(udpSocketReceiver)
        }*/
    }

    /*inner class UdpBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context, "${intent?.getStringExtra("MESSAGE_STRING")}", Toast.LENGTH_SHORT).show()
            Log.e("UdpReceiver", "message received: ${intent?.getStringExtra("MESSAGE_STRING")}")
            //udpSender.sendPacket("hi")
            udpListerner?.onUdpReceived()
        }
    }*/
}

