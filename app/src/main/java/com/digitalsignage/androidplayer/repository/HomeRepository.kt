package com.digitalsignage.androidplayer.repository

import com.digitalsignage.androidplayer.model.request.LoginRequest
import com.digitalsignage.androidplayer.model.response.LoginResponse
import com.digitalsignage.androidplayer.remote.ApiInterface
import com.digitalsignage.androidplayer.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

@ExperimentalCoroutinesApi
object HomeRepository  {

    fun getLogin(request: LoginRequest): Flow<State<LoginResponse>> =
        object : NetworkBoundRepository<LoginResponse>() {

            override suspend fun fetchFromRemote(): Response<LoginResponse> =
                ApiInterface.mServices.login(request)
        }.asFlow().flowOn(Dispatchers.IO)

}