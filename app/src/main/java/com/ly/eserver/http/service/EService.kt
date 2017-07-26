package com.ly.eserver.http.service

import com.ly.eserver.bean.DataBean
import com.ly.eserver.bean.OperlogBean
import com.ly.eserver.bean.ProjectBean
import com.ly.eserver.bean.UserBean
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by quantan.liu on 2017/3/22.
 */

interface EService {
    companion object {
        val API_URL = "http://192.168.1.204:8080/eserver/"
    }

    @POST("findUser")
    fun login(@Body user :UserBean): Observable<DataBean<UserBean>>

    @POST("updateUser")
    fun changePwd(@Body user :UserBean): Observable<DataBean<UserBean>>

    @GET("findprojectByid/{projectid}")
    fun findProject(@Path("projectid") type: Int):Observable<DataBean<ProjectBean>>
    /**
     * 参数：operlogin
     * 例 http://localhost:8080/eserver/deleteOperlog/1
     */
    @GET("deleteOperlog/{operlogid}")
    fun deleteOperlog(@Path("operlogin") type: Int):Observable<DataBean<OperlogBean>>
}
