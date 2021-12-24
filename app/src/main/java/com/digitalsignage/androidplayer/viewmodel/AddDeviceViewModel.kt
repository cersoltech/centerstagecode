package com.digitalsignage.androidplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digitalsignage.androidplayer.base.BaseViewModel
import com.digitalsignage.androidplayer.utils.State
import com.digitalsignage.androidplayer.model.request.AddDeviceRequest
import com.digitalsignage.androidplayer.model.response.AddDeviceResponse
import com.digitalsignage.androidplayer.repository.AddDeviceRepository
import com.digitalsignage.androidplayer.utils.AppLog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class AddDeviceViewModel : BaseViewModel() {

    companion object{
        private val tag = "AddDevice"
    }

    //Create a cancellable job
    private var job: Job? = null

    private val _addDeviceData = MutableLiveData<State<AddDeviceResponse>>()
    val addDeviceData : LiveData<State<AddDeviceResponse>>
        get() = _addDeviceData


    fun addDeviceCall(data: AddDeviceRequest){

        viewModelScope.launch {
            AppLog.i(tag,"add device called")

            AddDeviceRepository.addDevice(data).collect {
                when (it)
                {
                    is State.Loading -> {
                        AppLog.i(tag,"add device loading")
                    }

                    is State.Error -> {
                        AppLog.i(tag,"add device error")
                        _addDeviceData.value = it
                    }

                    is State.Success -> {
                        AppLog.i(tag,"add device success")
                        _addDeviceData.value = it
                    }
                }
            }
        }
    }

}