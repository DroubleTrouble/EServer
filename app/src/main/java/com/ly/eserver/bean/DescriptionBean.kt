package com.ly.eserver.bean

import java.io.Serializable
import java.util.*

/**
 * 说明
 * Created by Max on 2017/8/11.
 */
data class DescriptionBean constructor(
        val descripid: Int = 0, //主键
        val userid: Int = 0, //用戶表外鍵
        val location: String? = null, //定位：200.101
        val picture1: String? = null, //图片地址
        val picture2: String? = null, //图片地址
        val description: String? = null, //说明
        val time: Date? = null   //时间：YYYY-MM-DD:HH-MM-SS
) : Serializable {

}