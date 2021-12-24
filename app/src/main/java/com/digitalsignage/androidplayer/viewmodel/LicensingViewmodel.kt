package com.digitalsignage.androidplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digitalsignage.androidplayer.base.BaseViewModel
import com.digitalsignage.androidplayer.model.LicenseResponse
import com.digitalsignage.androidplayer.model.response.GetTemplatesResponse
import com.digitalsignage.androidplayer.repository.AddDeviceRepository
import com.digitalsignage.androidplayer.repository.SplashRepository
import com.digitalsignage.androidplayer.utils.AppLog
import com.digitalsignage.androidplayer.utils.LOADER_STATE
import com.digitalsignage.androidplayer.utils.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class LicensingViewmodel: BaseViewModel() {

    companion object{
        private val tag = "license"
    }

    //Create a cancellable job
    private var job: Job? = null

    private val _getLicenseData = MutableLiveData<State<LicenseResponse>>()
    val getLicenseData : LiveData<State<LicenseResponse>>
        get() = _getLicenseData


    fun getLicenseApiData(imeiNo: String){
        job?.cancel()
        viewModelScope.launch {
            AddDeviceRepository.getDeviceLicense(imeiNo).collect {
                when (it) {
                    is State.Loading ->{
                        setLoadingState(LOADER_STATE.LOADING)
                        AppLog.d(tag,"Loading")

                    }

                    is State.Success -> {
                        setLoadingState(LOADER_STATE.SUCCESS)
                        if (it.code == 200)
                        {
                            _getLicenseData.value = it
                        }
                    }

                    is State.Error -> {
                        //setLoadingState(LOADER_STATE.ERROR, VialisApp.instance.getString(R.string.no_data_available_text))
                        setLoadingState(LOADER_STATE.ERROR, "error")
                        AppLog.d(tag,
                                "<" + it.message + "> <" + it.code.toString() + ">")
                    }
                }
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}