package com.ly.eserver.bean

import java.io.Serializable
import java.util.*

/**
 * 操作日志
 * Created by Max on 2017/7/5.
 */
data class OperlogBean constructor(
        var operlogid: Int? = null, //主键
        var userid: Int? = null, //用户表外键
        var tableAddress: String? = null, //表地址
        var location: String? = null, //定位：200.101
        var address: String? = null, //地址，江苏省南京市鼓楼区
        var type: String? = null, //协议类型
        var isfinish: Boolean = false, //是否完成
        var result: String? = null, //抄读结果
        var issended : Boolean = false, //是否已发送到服务器
        var time: Date? = null   //时间：YYYY-MM-DD:HH-MM-SS
) : Serializable {

}