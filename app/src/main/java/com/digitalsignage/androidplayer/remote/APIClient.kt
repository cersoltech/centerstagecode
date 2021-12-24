package com.digitalsignage.androidplayer.remote


import android.os.Build
import com.digitalsignage.androidplayer.App
import com.digitalsignage.androidplayer.BuildConfig
import com.digitalsignage.androidplayer.remote.interceptor.NetworkInterceptor
import com.digitalsignage.androidplayer.utils.AppLog
import com.digitalsignage.androidplayer.utils.Constants
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class APIClient {
    @ExperimentalCoroutinesApi
    companion object {
        internal const val MAXIMUM_RETRY = 4
        internal var currentTry = 0

        //Build Retrofit Client
        fun getClient(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(getClient1().build())
                .build()
        }



        /**
         * Returns an unsafe OkHttpClient that accepts all certificates
         */
        //TODO 5/10/2020 : Use actual certificates for Release
        fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            try {
                //Get list of trusted certificates
                val trustAllCerts = getTrustedCertificates()

                //Get SSL Certificates
                val sslSocketFactory = getUnsafeSSlCertificate(trustCertificates = trustAllCerts)

                return OkHttpClient.Builder().apply {
                    sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    hostnameVerifier { hostname, session -> true }
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        /**
         * This method returns a OkHttpClient required for application with the necessary loggers/interceptors applied
         */
        private fun getOkHttpClient(): OkHttpClient.Builder {
            try {
                val logging = HttpLoggingInterceptor {
                    AppLog.d("APlayer_API", it)
                }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                return OkHttpClient.Builder().apply {
                    //addInterceptor(HeaderInterceptor())
                    addInterceptor(NetworkInterceptor(App.getInstance()))
                    connectTimeout(15, TimeUnit.SECONDS)
                    readTimeout(2, TimeUnit.MINUTES)
                    writeTimeout(15, TimeUnit.SECONDS)
                    if (BuildConfig.DEBUG) {
                        addInterceptor(logging)
                        addInterceptor(OkHttpProfilerInterceptor())
                    }
                    //addInterceptor(AuthInterceptor())
                }
               /* return getUnsafeOkHttpClient().apply {
                    //addInterceptor(HeaderInterceptor())
                    addNetworkInterceptor(NetworkInterceptor(App.getInstance()))
                    connectTimeout(15, TimeUnit.SECONDS)
                    readTimeout(2, TimeUnit.MINUTES)
                    writeTimeout(15, TimeUnit.SECONDS)
                    if (BuildConfig.DEBUG) {
                        addInterceptor(logging)
                        addInterceptor(OkHttpProfilerInterceptor())
                    }
                    //addInterceptor(AuthInterceptor())
                }*/
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }


         fun getClient1(): OkHttpClient.Builder{
            var client: OkHttpClient.Builder? = OkHttpClient.Builder()

             val logging = HttpLoggingInterceptor {
                 AppLog.d("APlayer_API", it)
             }.apply {
                 level = HttpLoggingInterceptor.Level.BODY
             }
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    client = OkHttpClient.Builder().apply {
                        addInterceptor(NetworkInterceptor(App.getInstance()))
                        connectTimeout(15, TimeUnit.SECONDS)
                        readTimeout(2, TimeUnit.MINUTES)
                        writeTimeout(15, TimeUnit.SECONDS)
                        if (BuildConfig.DEBUG) {
                            addInterceptor(logging)
                            addInterceptor(OkHttpProfilerInterceptor())
                        }
                    }
                }else
                {
                    val tlsSocketFactory = TLSSocketFactory()
                    if (tlsSocketFactory.trustManager != null) {
                        client = OkHttpClient.Builder().apply {
                            sslSocketFactory(tlsSocketFactory, tlsSocketFactory.trustManager)
                            addInterceptor(NetworkInterceptor(App.getInstance()))
                            connectTimeout(15, TimeUnit.SECONDS)
                            readTimeout(2, TimeUnit.MINUTES)
                            writeTimeout(15, TimeUnit.SECONDS)
                            if (BuildConfig.DEBUG) {
                                addInterceptor(logging)
                                addInterceptor(OkHttpProfilerInterceptor())
                            }
                        }

                    }
                }
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            }
             return client!!
        }
        /**
         * This method returns a SSL Certificate that accepts all types of SSL certificate
         */
        fun getUnsafeSSlCertificate(trustCertificates: Array<TrustManager>): SSLSocketFactory {

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustCertificates, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            return sslContext.socketFactory
        }

        /**
         * returns a list of all trusted certificates
         */
        private fun getTrustedCertificates(): Array<TrustManager> =
            arrayOf<TrustManager>(object : X509TrustManager {
                // Create a trust manager that currently does not validate certificate chains

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }


                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }
            })

    }


}