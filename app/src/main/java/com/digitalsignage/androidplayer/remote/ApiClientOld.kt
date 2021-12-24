package com.digitalsignage.androidplayer.remote

import com.digitalsignage.androidplayer.App
import com.digitalsignage.androidplayer.remote.interceptor.HeaderInterceptor
import com.digitalsignage.androidplayer.remote.interceptor.NetworkInterceptor
import com.digitalsignage.androidplayer.utils.AppLog
import com.digitalsignage.androidplayer.utils.Constants
import com.itkacher.okprofiler.BuildConfig
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClientOld {

    @ExperimentalCoroutinesApi
    companion object{
        private var loggingInterceptor = HttpLoggingInterceptor{
            AppLog.d("AP_API",it)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }


        fun getClient(): ApiInterface{

            /**
             * This method returns a OkHttpClient required for application with the necessary loggers/interceptors applied
             */
            val client = OkHttpClient.Builder()
                .connectTimeout(60,TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(15,TimeUnit.SECONDS)
                .addInterceptor(NetworkInterceptor(App.getInstance()))
                .addInterceptor(OkHttpProfilerInterceptor())
                .addInterceptor{chain ->  HeaderInterceptor().intercept(chain)}
                    if (BuildConfig.DEBUG)
                    {
                        client.addInterceptor(loggingInterceptor)
                    }

            //Build Retrofit Client
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .client(client.build())
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}