package com.digitalsignage.androidplayer.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.digitalsignage.androidplayer.databinding.LayoutLoaderBinding
import com.digitalsignage.androidplayer.utils.LOADER_STATE
import com.digitalsignage.androidplayer.utils.PreferanceRepository
import com.digitalsignage.androidplayer.utils.gone
import com.digitalsignage.androidplayer.utils.visible

abstract class BaseFragment<T: ViewDataBinding,V: BaseViewModel>: Fragment() {

    var mBinding: T? = null
    var mViewModel: V? = null

    abstract fun getViewModel(): V
    abstract fun getFragmentLayout(): Int
    abstract fun getBindingVariable(): Int

    fun initBinding(inflater: LayoutInflater,container: ViewGroup?): View {
        mBinding = DataBindingUtil.inflate(inflater,getFragmentLayout(),container,false)
        mViewModel = getViewModel()
        mBinding?.setVariable(getBindingVariable(),mViewModel)

        return mBinding!!.root
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

}