package com.ly.eserver.bean

import java.util.Date

data class TaskBean constructor(
    val taskid: Int? = null,                //主键
    val userid: Int? = null,            //工号
    val tableAddress: String? = null,//表地址
    val location: String? = null,    //定位，例：175.125
    val address: String? = null,        //地址，例：江苏南京
    val type: String? = null,        //表类型
    val isIsfinish: Boolean = false,    //是否完成
    val isIsadd: Boolean = false,        //是否新增
    val starttime: Date? = null,        //开始时间
    val endtime: Date? = null        //结束时间
)
