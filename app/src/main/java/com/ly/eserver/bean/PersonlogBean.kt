
package com.ly.eserver.bean

import java.io.Serializable
import java.util.Date

data class PersonlogBean constructor(
        var personlogid: Int? = null, //主键
        var userid: Int? = null,        //用户表外键
        var projectid: Int? = null,    //项目表外键
        var department: String? = null,    //部门
        var area: String? = null,    //所在地
        var accommodation: Double? = null,    //住宿费
        var travel: Double? = null,    //车船费
        var food: Double? = null,    //伙补费
        var vehicle: Double? = null,    //车辆费用
        var office: Double? = null,    //办事处费用
        var material: Double? = null,    //材料费
        var lowValueConsumables: Double? = null,    //低值易耗品
        var courier: Double? = null,    //快递费
        var hospitality: Double? = null,    //招待费
        var administrative: Double? = null,    //办公费
        var welfare: Double? = null,    //福利费
        var others: Double? = null,   //其它费用
        var personlog: String? = null,    //个人日志内容
        var time: Date? = null        //创建时间
): Serializable
