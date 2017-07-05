package com.ly.eserver.bean

import java.io.Serializable

/**
 * Created by Max on 2017/7/5.
 */
data class UserBean constructor(
        val userid: Int = 0, //主键，工号
        val username: String? = null, //用户名（姓名）
        val password: String? = null, //密码
        val projectid: String? = null, //项目号
        val phone: String? = null, //手机号
        val PIN1: String? = null, //设备号1
        val PIN2: String? = null, //设备号2
        val department: String? = null    //部门
) : Serializable {

}

