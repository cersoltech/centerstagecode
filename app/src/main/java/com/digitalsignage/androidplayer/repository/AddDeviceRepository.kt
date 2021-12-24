package com.digitalsignage.androidplayer.repository

import com.digitalsignage.androidplayer.model.LicenseResponse
import com.digitalsignage.androidplayer.utils.State
import com.digitalsignage.androidplayer.model.request.AddDeviceRequest
import com.digitalsignage.androidplayer.model.response.AddDeviceResponse
import com.digitalsignage.androidplayer.remote.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

@ExperimentalCoroutinesApi
object AddDeviceRepository {

    fun addDevice(request: AddDeviceRequest): Flow<State<AddDeviceResponse>> =
        object : NetworkBoundRepository<AddDeviceResponse>() {

            override suspend fun fetchFromRemote(): Response<AddDeviceResponse> =
                ApiInterface.mServices.addDevice(request)

        }.asFlow().flowOn(Dispatchers.IO)

    fun getDeviceLicense(deviceImei: String): Flow<State<LicenseResponse>> =
            object : NetworkBoundRepository<LicenseResponse>(){
                override suspend fun fetchFromRemote(): Response<LicenseResponse> =
                     ApiInterface.mServices.checkLicense(deviceImei)
            }.asFlow().flowOn(Dispatchers.IO)
}