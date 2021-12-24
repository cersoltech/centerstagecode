package com.digitalsignage.androidplayer.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digitalsignage.androidplayer.base.BaseViewModel
import com.digitalsignage.androidplayer.model.LicenseResponse
import com.digitalsignage.androidplayer.model.request.AddDeviceRequest
import com.digitalsignage.androidplayer.model.request.LoginRequest
import com.digitalsignage.androidplayer.model.response.*
import com.digitalsignage.androidplayer.repository.AddDeviceRepository
import com.digitalsignage.androidplayer.repository.LoginRepository
import com.digitalsignage.androidplayer.utils.AppLog
import com.digitalsignage.androidplayer.utils.LOADER_STATE
import com.digitalsignage.androidplayer.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class LoginViewModel: BaseViewModel() {

    companion object{
        private val tag = "DeviceSelection"
    }

    //Create a cancellable job
    private var job: Job? = null

    private val _loginData = MutableLiveData<Boolean>()
    val loginData : LiveData<Boolean>
        get() = _loginData

    private val _zoneData = MutableLiveData<State<ZoneResponse>>()
    val zoneData : LiveData<State<ZoneResponse>>
     get() = _zoneData

    private val _cityData = MutableLiveData<State<CitiesResponse>>()
    val cityData : LiveData<State<CitiesResponse>>
     get() = _cityData

    private val _branchData = MutableLiveData<State<BranchesResponse>>()
    val branchData : LiveData<State<BranchesResponse>>
        get() = _branchData

    private val _deviceGroupData = MutableLiveData<State<DeviceGroupResponse>>()
    val deviceGroupData : LiveData<State<DeviceGroupResponse>>
        get() = _deviceGroupData

    private val _deviceData = MutableLiveData<State<DeviceResponse>>()
    val deviceData : LiveData<State<DeviceResponse>>
        get() = _deviceData

    private val _assetDownloadScheduleData = MutableLiveData<State<AssetDownloadScheduleResponse>>()
    val assetDownloadScheduleData : LiveData<State<AssetDownloadScheduleResponse>>
        get() = _assetDownloadScheduleData

    private val _addDeviceData = MutableLiveData<State<AddDeviceResponse>>()
    val addDeviceData : LiveData<State<AddDeviceResponse>>
        get() = _addDeviceData

    private val _getLicenseData = MutableLiveData<State<LicenseResponse>>()
    val getLicenseData : LiveData<State<LicenseResponse>>
        get() = _getLicenseData

    init {
        //getAssetDownloadScheduleData("31-05-2021")
        //getZonesData()
    }

    fun getAssetDownloadScheduleData(date: String){
        //job?.cancel()
        job = viewModelScope.launch {
            LoginRepository.getAssetDownloadSchedule(date = date).collect {
                when (it) {
                    is State.Loading ->{
                        setLoadingState(LOADER_STATE.LOADING)
                        AppLog.d(tag,"Loading")

                    }

                    is State.Success -> {
                        setLoadingState(LOADER_STATE.SUCCESS)
                        if (it.code == 200)
                        {
                            _assetDownloadScheduleData.value = it
                            //AppLog.d(tag,it.data.toString())
                        }
                    }

                    is State.Error -> {
                        //setLoadingState(LOADER_STATE.ERROR, VialisApp.instance.getString(R.string.no_data_available_text))
                        setLoadingState(LOADER_STATE.ERROR, "error")
                        AppLog.d(
                            tag,
                            "<" + it.message + "> <" + it.code.toString() + ">"
                        )
                    }
                }
            }
        }
    }

    fun getZonesData() {
        //job?.cancel()

        job = viewModelScope.launch {
            LoginRepository.getZonesList().collect {
                when (it) {
                    is State.Loading -> {
                        setLoadingState(LOADER_STATE.LOADING)
                        AppLog.d(tag, "Loading")

                    }

                    is State.Success -> {
                        setLoadingState(LOADER_STATE.SUCCESS)
                        if (it.code == 200) {
                            _zoneData.value = it
                        }
                    }

                    is State.Error -> {
                        //setLoadingState(LOADER_STATE.ERROR, VialisApp.instance.getString(R.string.no_data_available_text))
                        setLoadingState(LOADER_STATE.ERROR, "error")
                        AppLog.d(
                                tag,
                                "<" + it.message + "> <" + it.code.toString() + ">"
                        )
                    }
                }
            }
        }
    }

    fun getCitiesData(zoneId: String){
        job?.cancel()

        job = viewModelScope.launch {
            LoginRepository.getCitiesList(zoneId = zoneId).collect {
                when (it) {
                    is State.Loading ->{
                        setLoadingState(LOADER_STATE.LOADING)
                        AppLog.d(tag,"Loading")
                    }

                    is State.Success -> {
                        setLoadingState(LOADER_STATE.SUCCESS)
                        if (it.code == 200)
                        {
                            _cityData.value = it
                            //AppLog.d(tag,it.data.toString())
                        }
                    }

                    is State.Error -> {
                        //setLoadingState(LOADER_STATE.ERROR, VialisApp.instance.getString(R.string.no_data_available_text))
                        setLoadingState(LOADER_STATE.ERROR, "error")
                        AppLog.d(
                            tag,
                            "<" + it.message + "> <" + it.code.toString() + ">"
                        )
                    }
                }
            }
        }
    }

    fun getBranchData(cityId: String){
        job?.cancel()

        job = viewModelScope.launch {
            LoginRepository.getBranchList(cityId = cityId).collect {
                when (it) {
                    is State.Loading ->{
                        setLoadingState(LOADER_STATE.LOADING)
                        AppLog.d(tag,"Loading")

                    }

                    is State.Success -> {
                        setLoadingState(LOADER_STATE.SUCCESS)
                        if (it.code == 200)
                        {
                            _branchData.value = it
                            //AppLog.d(tag,it.data.toString())
                        }
                    }

                    is State.Error -> {
                        //setLoadingState(LOADER_STATE.ERROR, VialisApp.instance.getString(R.string.no_data_available_text))
                        setLoadingState(LOADER_STATE.ERROR, "error")
                        AppLog.d(
                            tag,
                            "<" + it.message + "> <" + it.code.toString() + ">"
                        )
                    }
                }
            }
        }
    }

    fun getDeviceGroupData(branchId: String){
        job?.cancel()

        job = viewModelScope.launch {
            LoginRepository.getDeviceGroupList(branchId = branchId).collect {
                when (it) {
                    is State.Loading ->{
                        setLoadingState(LOADER_STATE.LOADING)
                        AppLog.d(tag,"Loading")

                    }

                    is State.Success -> {
                        setLoadingState(LOADER_STATE.SUCCESS)
                        if (it.code == 200)
                        {
                            _deviceGroupData.value = it
                            //AppLog.d(tag,it.data.toString())
                        }
                    }

                    is State.Error -> {
                        //setLoadingState(LOADER_STATE.ERROR, VialisApp.instance.getString(R.string.no_data_available_text))
                        setLoadingState(LOADER_STATE.ERROR, "error")
                        AppLog.d(
                            tag,
                            "<" + it.message + "> <" + it.code.toString() + ">"
                        )
                    }
                }
            }
        }
    }

    fun getDeviceData(){
        job?.cancel()

        job = viewModelScope.launch {
            LoginRepository.getDeviceList().collect {
                when (it) {
                    is State.Loading ->{
                        setLoadingState(LOADER_STATE.LOADING)
                        AppLog.d(tag,"Loading")

                    }

                    is State.Success -> {
                        setLoadingState(LOADER_STATE.SUCCESS)
                        if (it.code == 200)
                        {
                            _deviceData.value = it
                            //AppLog.d(tag,it.data.toString())
                        }
                    }

                    is State.Error -> {
                        //setLoadingState(LOADER_STATE.ERROR, VialisApp.instance.getString(R.string.no_data_available_text))
                        setLoadingState(LOADER_STATE.ERROR, "error")
                        AppLog.d(
                            tag,
                            "<" + it.message + "> <" + it.code.toString() + ">"
                        )
                    }
                }
            }
        }
    }

    fun getLoginData(context: Activity){
        viewModelScope.launch(Dispatchers.IO){
            LoginRepository.getLogin(
                LoginRequest()
            ).collect {
                when (it) {
                    is State.Loading -> AppLog.d(tag,"Loading")

                    is State.Success -> {
                        if (it.code == 200)
                        {
                            it.data.let {
                                loginResponse ->

                            }
                            AppLog.d(tag,it.data.toString())
                        }
                    }

                    is State.Error -> {
                        AppLog.d(
                            tag,
                            "<" + it.message + "> <" + it.code.toString() + ">"
                        )
                    }
                }
            }
        }
    }

    fun getAddDeviceData(deviceIP: String,deviceName: String,deviceMacAddress: String,deviceHt: String,deviceWidth: String){
        //job?.cancel()
        val request = AddDeviceRequest(
                deviceIP,deviceName,deviceMacAddress,deviceHt,deviceWidth)
        job = viewModelScope.launch {
            LoginRepository.addDevice(request).collect {
                when (it) {
                    is State.Loading ->{
                        setLoadingState(LOADER_STATE.LOADING)
                        AppLog.d(tag,"Loading")

                    }

                    is State.Success -> {
                        setLoadingState(LOADER_STATE.SUCCESS)
                        if (it.code == 200)
                        {
                            _addDeviceData.value = it
                            //AppLog.d(tag,it.data.toString())
                        }
                    }

                    is State.Error -> {
                        //setLoadingState(LOADER_STATE.ERROR, VialisApp.instance.getString(R.string.no_data_available_text))
                        setLoadingState(LOADER_STATE.ERROR, "error")
                        AppLog.d(
                            tag,
                            "<" + it.message + "> <" + it.code.toString() + ">"
                        )
                    }
                }
            }
        }
    }

    fun getLicenseApiData(imeiNo: String){
        //job?.cancel()
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

    fun getZoneSource(): Flow<State<ZoneResponse>>{
        return LoginRepository.getZonesList()
    }

}