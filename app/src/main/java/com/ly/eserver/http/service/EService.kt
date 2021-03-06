package com.ly.eserver.http.service

import com.ly.eserver.bean.*
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.*

/**
 * Created by quantan.liu on 2017/3/22.
 */

interface EService {
    companion object {
        val API_URL = "http://192.168.1.204:8080/eserver/"
    }
    /**
     * 参数：operlogid
     * 例 http://localhost:8080/eserver/operlog/delete/Operlogid
     */
    @GET("operlog/delete/{operlogid}")
    fun deleteOperlog(@Path("operlogin") type: Int) : Observable<DataBean<OperlogBean>>

    @POST("operlog/insert")
    fun insertOperlog(@Body operlog: OperlogBean) : Observable<DataBean<OperlogBean>>

    @POST("user/login")
    fun login(@Body user :UserBean): Observable<DataBean<UserBean>>

    @POST("user/update")
    fun changePwd(@Body user :UserBean): Observable<DataBean<UserBean>>

    @GET("project/findByid/{projectid}")
    fun findProject(@Path("projectid") type: Int):Observable<DataBean<ProjectBean>>

    @POST("personlog/insert")
    fun insertPersonlog(@Body personlogBean: PersonlogBean):Observable<DataBean<PersonlogBean>>

    @POST("description/insert")
    fun insertDescription(@Body descriptionBean: DescriptionBean) : Observable<DataBean<DescriptionBean>>

    @GET("description/qiniu_token")
    fun getQiniuToken() : Observable<DataBean<String>>

    @POST("check/findByIdAndTime")
    fun findCheckByIdAndTime(@Body checkBean: CheckBean) : Observable<DataBean<Boolean>>

    @POST("check/insert")
    fun insertCheck(@Body checkBean: CheckBean) : Observable<DataBean<CheckBean>>

    @GET("eserver.apk")
    fun download() : Observable<RequestBody>

    @GET("apk/version")
    fun getVersion() : Observable<DataBean<VersionBean>>
}
