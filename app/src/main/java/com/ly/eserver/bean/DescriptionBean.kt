package com.ly.eserver.bean

import java.io.Serializable
import java.util.*

/**
 * 说明
 * Created by Max on 2017/8/11.
 */
data class DescriptionBean constructor(
        val descripid: Int = 0, //主键
        var userid: Int = 0, //用戶表外鍵
        var projectid : Int? = null,
        var location: String? = null, //定位：200.101
        var picture1: String? = null, //图片地址
        var picture2: String? = null, //图片地址
        var description: String? = null, //说明
        var time: Date? = null   //时间：YYYY-MM-DD:HH-MM-SS
) : Serializable