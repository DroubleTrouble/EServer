package com.ly.eserver.bean
import java.io.Serializable

/**
 * Created by Max on 2017/7/5.
 */
data class UserBean constructor(
        var userid: Int? = null, //主键，工号
        var username: String? = null, //用户名（姓名）
        var password: String? = null, //密码
        var projectid: Int? = null, //项目号
        var phone: String? = null, //手机号
        var PIN1: String? = null, //设备号1
        var PIN2: String? = null, //设备号2
        var department: String? = null    //部门
) : Serializable {

}

