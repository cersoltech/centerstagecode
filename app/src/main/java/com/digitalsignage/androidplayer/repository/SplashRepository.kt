package com.digitalsignage.androidplayer.repository

import com.digitalsignage.androidplayer.model.response.AssetDownloadScheduleResponse
import com.digitalsignage.androidplayer.model.response.GetTemplatesResponse
import com.digitalsignage.androidplayer.remote.ApiInterface
import com.digitalsignage.androidplayer.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

@ExperimentalCoroutinesApi
object SplashRepository {
    fun getAllTemplateData(): Flow<State<GetTemplatesResponse>> =
            object : NetworkBoundRepository<GetTemplatesResponse>() {

                override suspend fun fetchFromRemote(): Response<GetTemplatesResponse> =
                        ApiInterface.mServices.getAllDeviceTemplates()
            }.asFlow().flowOn(Dispatchers.IO)



}