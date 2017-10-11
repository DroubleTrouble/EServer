package com.ly.eserver.bean

import java.io.Serializable
import java.util.*

/**
 * 签到
 * Created by Max on 2017/8/11.
 */
data class CheckBean constructor(
        var checkno: Int = 0, //主键
        var userid: Int = 0, //用戶表外鍵
        var ischeck : Boolean = false,
        var location: String? = null, //主键
        var address: String? = null, //主键
        var time: Date? = null   //时间：YYYY-MM-DD:HH-MM-SS
) : Serializable