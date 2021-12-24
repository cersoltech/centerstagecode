package com.digitalsignage.androidplayer.repository

import com.digitalsignage.androidplayer.model.request.AddDeviceRequest
import com.digitalsignage.androidplayer.utils.State
import com.digitalsignage.androidplayer.model.request.LoginRequest
import com.digitalsignage.androidplayer.model.response.*
import com.digitalsignage.androidplayer.remote.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

@ExperimentalCoroutinesApi
object LoginRepository {

    /**
     * Fetches the Login/Declare data from network and emits it
     */
    fun getLogin(request: LoginRequest): Flow<State<LoginResponse>> =
        object : NetworkBoundRepository<LoginResponse>() {

            override suspend fun fetchFromRemote(): Response<LoginResponse> =
                ApiInterface.mServices.login(request)
        }.asFlow().flowOn(Dispatchers.IO)

    fun getAssetDownloadSchedule(date: String): Flow<State<AssetDownloadScheduleResponse>> =
        object : NetworkBoundRepository<AssetDownloadScheduleResponse>() {

            override suspend fun fetchFromRemote(): Response<AssetDownloadScheduleResponse> =
                ApiInterface.mServices.getAssetDownloadSchedules(date = date)
        }.asFlow().flowOn(Dispatchers.IO)


    fun getZonesList(): Flow<State<ZoneResponse>> =
            object : NetworkBoundRepository<ZoneResponse>() {

                override suspend fun fetchFromRemote(): Response<ZoneResponse> =
                    ApiInterface.mServices.getZones()
            }.asFlow().flowOn(Dispatchers.IO)

    fun getCitiesList(zoneId: String): Flow<State<CitiesResponse>> =
        object : NetworkBoundRepository<CitiesResponse>() {

            override suspend fun fetchFromRemote(): Response<CitiesResponse> =
              ApiInterface.mServices.getCities(zone_id = zoneId)

        }.asFlow().flowOn(Dispatchers.IO)

    fun getBranchList(cityId: String): Flow<State<BranchesResponse>> =
        object : NetworkBoundRepository<BranchesResponse>() {

            override suspend fun fetchFromRemote(): Response<BranchesResponse> =
                ApiInterface.mServices.getBranches(city_id = cityId)

        }.asFlow().flowOn(Dispatchers.IO)

    fun getDeviceGroupList(branchId: String): Flow<State<DeviceGroupResponse>> =
        object : NetworkBoundRepository<DeviceGroupResponse>() {

            override suspend fun fetchFromRemote(): Response<DeviceGroupResponse> =
                ApiInterface.mServices.getDeviceGroups(branch_id = branchId)

        }.asFlow().flowOn(Dispatchers.IO)

    fun getDeviceList(): Flow<State<DeviceResponse>> =
        object : NetworkBoundRepository<DeviceResponse>() {

            override suspend fun fetchFromRemote(): Response<DeviceResponse> =
                ApiInterface.mServices.getDevices()

        }.asFlow().flowOn(Dispatchers.IO)

    fun addDevice(request: AddDeviceRequest): Flow<State<AddDeviceResponse>> =
        object : NetworkBoundRepository<AddDeviceResponse>() {
            override suspend fun fetchFromRemote(): Response<AddDeviceResponse> =
                   ApiInterface.mServices.addDevice(request)
        }.asFlow().flowOn(Dispatchers.IO)
}