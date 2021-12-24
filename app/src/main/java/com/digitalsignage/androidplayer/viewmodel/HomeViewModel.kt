package com.digitalsignage.androidplayer.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digitalsignage.androidplayer.base.BaseViewModel
import com.digitalsignage.androidplayer.fileutils.ExternalFileHandling
import com.digitalsignage.androidplayer.fileutils.UdpReceiver
import com.digitalsignage.androidplayer.model.response.AssetDownloadScheduleResponse
import com.digitalsignage.androidplayer.model.response.ScheduleData
import com.digitalsignage.androidplayer.repository.LoginRepository
import com.digitalsignage.androidplayer.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class HomeViewModel : BaseViewModel(){

    private val TAG = "homeviewmodel"

    companion object{
        var fileNameUrl: String = ""
    }

    //Create a cancellable job
    private var job1: Job? = null

    val requiredAllDownload = MutableLiveData<Boolean>()
    val checkFileDiffRequired = MutableLiveData<Boolean>()
    private lateinit var oldTimeStamp: Number

    private val _assetDownloadScheduleData = MutableLiveData<AssetDownloadScheduleResponse>()
    val assetDownloadScheduleData : LiveData<AssetDownloadScheduleResponse>
        get() = _assetDownloadScheduleData

    private val _assetDownloadUrls = MutableLiveData<ArrayList<ScheduleData>>()
    val assetDownloadUrls : LiveData<ArrayList<ScheduleData>>
        get() = _assetDownloadUrls

    fun callAssetDownloadRepeat(){
        job1 = viewModelScope.launch {
             oldTimeStamp = System.currentTimeMillis()
            LoginRepository.getAssetDownloadSchedule(date = getCurrentdate("dd-MM-yyyy"))
                    .collect {
                        when (it) {
                            is State.Loading -> {
                                setLoadingState(LOADER_STATE.LOADING)
                                //AppLog.d(LoginViewModel.tag, "Loading")

                            }

                            is State.Success -> {
                                setLoadingState(LOADER_STATE.SUCCESS)
                                if (it.code == 200) {
                                    Log.d(TAG, "callAssetDownloadRepeat: called")
                                   /* it.data.data?.let { dataList ->
                                        _assetDownloadUrls.value = ArrayList(dataList)
                                    }*/

                                    it.data?.let { dataList ->
                                        _assetDownloadScheduleData.value = dataList
                                    }

                                    //AppLog.d(tag,it.data.toString())
                                    //call here file check method
                                    //save temp layout id in pref & default value -1
                                    //success check temp layout id  if -1 then download all files
                                    //suceess if new layout id equals to temp layout id then chk file diff
                                    //success if new layout id not equal to temp layout id set pref to new layout id & download all files
                                    //onbackpress set temp layout id to -1 in pref
                                    //ischeck(it.data)
                                    val diff = System.currentTimeMillis()- oldTimeStamp.toDouble()

                                    if (diff >= 60000)
                                    {
                                        callAssetDownloadRepeat()
                                    }else
                                    {
                                        delay((60000 - diff).toLong())
                                        callAssetDownloadRepeat()
                                    }
                                }
                            }

                            is State.Error -> {
                                //setLoadingState(LOADER_STATE.ERROR, VialisApp.instance.getString(R.string.no_data_available_text))
                                setLoadingState(LOADER_STATE.ERROR, "error")
                               /* AppLog.d(
                                        LoginViewModel.tag,
                                        "<" + it.message + "> <" + it.code.toString() + ">"
                                )*/
                            }
                        }
                    }
        }
    }

     fun ischeck(response: AssetDownloadScheduleResponse) {
         //check files
        viewModelScope.launch {
            /*if (true)
            {
                //api call - pr downloader video
                requiredAllDownload.postValue(true)
            }else
            {*/
                val diff = System.currentTimeMillis()- oldTimeStamp.toDouble()

                if (diff >= 60000)
                {
                    callAssetDownloadRepeat()
                }else
                {
                    delay((60000 - diff).toLong())
                    callAssetDownloadRepeat()
                }
           // }
        }

    }

    fun getUdpFromViewmodel(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            UdpReceiver.listenUdpResult(context)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job1?.cancel()
        job1 = null
    }

    fun clearViewmodel(){
        job1?.cancel()
        job1 = null
    }

}