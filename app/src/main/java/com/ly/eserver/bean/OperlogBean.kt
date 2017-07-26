package com.ly.eserver.bean

import java.io.Serializable
import java.util.*

/**
 * Created by Max on 2017/7/5.
 */
data class OperlogBean constructor(
        val operlogid: String? = null, //主键
        val userid: String? = null, //用户表外键
        val tableAddress: String? = null, //表地址
        val location: String? = null, //定位：200.101
        val address: String? = null, //地址，江苏省南京市鼓楼区
        val type: String? = null, //表类型，水表、电表
        val isfinish: Boolean = false, //是否完成
        val result: String? = null, //抄读结果
        val time: Date? = null   //时间：YYYY-MM-DD:HH-MM-SS
) : Serializable {

}