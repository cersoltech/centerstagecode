package com.digitalsignage.androidplayer.base

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleRegistryOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.digitalsignage.androidplayer.App
import com.digitalsignage.androidplayer.R
import com.digitalsignage.androidplayer.databinding.LayoutLoaderBinding
import com.digitalsignage.androidplayer.model.ConnectionLiveData
import com.digitalsignage.androidplayer.utils.AppLog
import com.digitalsignage.androidplayer.utils.LOADER_STATE
import com.digitalsignage.androidplayer.utils.gone
import com.digitalsignage.androidplayer.utils.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.lang.Exception

/**
 * Abstract Activity which binds [ViewModel] [VM] and [ViewBinding] [VB]
 */
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
abstract class BaseActivity<T: ViewDataBinding,V: BaseViewModel>: AppCompatActivity(),ConnectionLiveData.NetworkReceiver.ConnectivityReceiverListener {

     var mBinding: T? = null
     var mViewModel:V? = null

    private var netConnectionReceiver: ConnectionLiveData.NetworkReceiver? = null
    private var mSnackBar: Snackbar? = null

    abstract fun getViewModel(): V
    abstract fun getContentLayout(): Int
    abstract fun getBindingVariable(): Int

     fun initBinding(){
       try {
       mBinding = DataBindingUtil.setContentView(this,getContentLayout())
        mViewModel = getViewModel()
        mBinding?.setVariable(getBindingVariable(),mViewModel)

        /*val connectionLiveData = ConnectionLiveData(App.getInstance())
           connectionLiveData.observe(this, Observer {
               if (it) {
                  Toast.makeText(this, "Internet Connection Available", Toast.LENGTH_SHORT).show()
                }else
               {
                   Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
               }
           })*/
       }catch (e: Exception)
       {
          AppLog.e("Bindingerror",e.stackTrace.toString())
       }
     }

    fun setUpLoader(view: LayoutLoaderBinding) {
        mViewModel?.loading?.observe(this, {
            it?.let {
                when (it) {
                    //When success, Hide both Loader & ErrorText
                    LOADER_STATE.SUCCESS -> {
                        view.pbLoader.gone()
                        //view.tvErrorText.gone()
                        //view.tvErrorImg.gone()
                    }
                    //When Loading, Show Loader & hide ErrorText
                    LOADER_STATE.LOADING -> {
                        view.pbLoader.visible()
                        //view.tvErrorText.gone()
                        //view.tvErrorImg.gone()
                    }
                    //When Error, Hide Loader & show/set ErrorText
                    LOADER_STATE.ERROR -> {
                        view.pbLoader.gone()
                        /*view.tvErrorText.text = mViewModel.error.value
                        view.tvErrorText.visible()
                        if (mViewModel.image.value.equals("")) {
                            view.tvErrorImg.gone()
                        } else {
                            view.tvErrorImg.visible()
                        }*/
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

        if (netConnectionReceiver == null) {
            netConnectionReceiver = ConnectionLiveData.NetworkReceiver()
        }
        App.getInstance().registerReceiver(netConnectionReceiver, filter)
    }

    override fun onResume() {
        super.onResume()
        ConnectionLiveData.NetworkReceiver.connectivityReceiverListener = this
    }

    override fun onStop() {
        super.onStop()
        if (netConnectionReceiver != null)
        {
            App.getInstance().unregisterReceiver(netConnectionReceiver)
        }
    }

    protected open fun fragmentTransaction(
        transactionType: Int,
        fragment: Fragment,
        container: Int,
        isAddToBackStack: Boolean,
        bundle: Bundle?
    ) {
        if (bundle != null) {
            fragment.arguments = bundle
        }

        val trans = supportFragmentManager.beginTransaction()
        when (transactionType) {
            ADD_FRAGMENT -> trans.add(container, fragment, fragment.javaClass.simpleName)
            REPLACE_FRAGMENT -> {
                trans.replace(container, fragment, fragment.javaClass.simpleName)
                if (isAddToBackStack) trans.addToBackStack(null)
            }
        }
        trans.commit()
    }

    companion object {
        const val ADD_FRAGMENT = 0
        const val REPLACE_FRAGMENT = 1
        //const val App_Version = "Version " + BuildConfig.VERSION_NAME
    }

    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {

            val messageToUser = "No Internet Connection"

            mSnackBar = Snackbar.make(findViewById(R.id.const_root), messageToUser, Snackbar.LENGTH_LONG) //Assume "rootLayout" as the root layout of every activity.
            mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
            mSnackBar?.setAction("OK",object : View.OnClickListener{
                override fun onClick(v: View?) {
                    mSnackBar?.dismiss()
                }

            })
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
        }
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }

}