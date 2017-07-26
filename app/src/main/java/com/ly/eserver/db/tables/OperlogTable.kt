package com.ly.eserver.db.tables

/**
 * 构建数据库表字段
 * Created by Max on 2017/7/25.
 */
object OperlogTable {
    val NAME = "operlog"
    val OPERLOGID = "operlogid"
    val USERID = "userid"
    val TABLEADDRESS = "tableAddress"
    val LOCATION = "location"   //经度 + 纬度
    val ADDRESS = "address"
    val TYPE = "type"
    val ISFINISH = "isfinish"
    val RESULT = "result"
    val TIME = "time"
}