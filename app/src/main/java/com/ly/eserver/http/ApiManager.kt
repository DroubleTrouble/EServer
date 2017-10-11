package com.ly.eserver.http

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ly.eserver.BuildConfig
import com.ly.eserver.http.service.EService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by zengwendi on 2017/6/12.
 * apiManager实现单列模式
 */
//主构造方法私有化
class ApiManager private constructor() {
    private var mService: EService? = null
    val logInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()

    companion object {
        private var mApiManager: ApiManager? = null
        val instence: ApiManager
            get() {
                if (mApiManager == null) {
                    synchronized(ApiManager::class) {
                        if (mApiManager == null) {
                            mApiManager = ApiManager()
                        }
                    }
                }
                return mApiManager!!
            }
    }

    val service: EService get() {
        if (mService == null) {

            val gson: Gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create()
            val retrofit = Retrofit.Builder()
                    .client(getClient(OkHttpClient.Builder()))
                    .baseUrl(EService.Companion.API_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            mService = retrofit.create<EService>(EService::class.java)
        }
        return mService!!
    }

    //OkHttpClient 赋值
    private fun getClient(builder: OkHttpClient.Builder): OkHttpClient {
        if (BuildConfig.DEBUG) {//显示日志
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        builder.hostnameVerifier { _, _ -> true }
        //设置超时
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS)
        builder.writeTimeout(10000, TimeUnit.MILLISECONDS)
        builder.readTimeout(10000, TimeUnit.MILLISECONDS)
        //错误重连
        builder.retryOnConnectionFailure(true)
        builder.addInterceptor(logInterceptor)//添加请求拦截
        return builder.build()
    }

}


