package com.digitalsignage.androidplayer.remote

import com.digitalsignage.androidplayer.model.LicenseResponse
import com.digitalsignage.androidplayer.model.request.AddDeviceRequest
import com.digitalsignage.androidplayer.model.request.LoginRequest
import com.digitalsignage.androidplayer.model.response.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @ExperimentalCoroutinesApi
    companion object {
        val mServices: ApiInterface by lazy {
            APIClient.getClient().create(ApiInterface::class.java)
        }
    }

    //https://devdss.swantech.ae/api/asset-download-schedules/?date=13-12-2020
    @GET("asset-download-schedules/")
    suspend fun getAssetDownloadSchedules(@Query("date") date: String): Response<AssetDownloadScheduleResponse>

    @GET("get-templates")
    suspend fun getAllDeviceTemplates(): Response<GetTemplatesResponse>

    //device selection
    @GET("zones/")
    suspend fun getZones(): Response<ZoneResponse>

    @GET("cities/")
    suspend fun getCities(@Query("zone_id") zone_id: String): Response<CitiesResponse>

    @GET("branches/")
    suspend fun getBranches(@Query("city_id") city_id: String): Response<BranchesResponse>

    @GET("device-groups/")
    suspend fun getDeviceGroups(@Query("branch_id") branch_id: String): Response<DeviceGroupResponse>

    @GET("devices/")
    suspend fun getDevices(): Response<DeviceResponse>

    @POST("devices")
    suspend fun addDevice(@Body request: AddDeviceRequest): Response<AddDeviceResponse>
    //http://devdss.swantech.ae/api/online-license?imei=d60502168b90dc7c

    @POST("online-license")
    suspend fun checkLicense(@Query("imei") imeiNo: String): Response<LicenseResponse>

    //Accounts
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

}