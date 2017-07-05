
package com.ly.eserver.bean

import java.util.Date

data class PersonlogBean constructor(
    val id: Int = 0, //主键
    val userid: Int = 0,        //用户表外键
    val projectid: Int = 0,    //项目表外键
    val department: String? = null,    //部门
    val area: String? = null,    //所在地
    val accommodation: Double? = null,    //住宿费
    val travel: Double? = null,    //车船费
    val food: Double? = null,    //伙补费
    val vehicle: Double? = null,    //车辆费用
    val office: Double? = null,    //办事处费用
    val material: Double? = null,    //材料费
    val lowValueConsumables: Double? = null,    //低值易耗品
    val courier: Double? = null,    //快递费
    val hospitality: Double? = null,    //招待费
    val administrative: Double? = null,    //办公费
    val welfare: Double? = null,    //福利费
    val others: Double? = null,   //其它费用
    val personlog: String? = null,    //个人日志内容
    val time: Date? = null        //创建时间
)
