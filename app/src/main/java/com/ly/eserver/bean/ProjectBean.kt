package com.ly.eserver.bean

import java.io.Serializable

data class ProjectBean constructor(
    val projectid: Int? = null,                //主键
    val area: String? = null,        //大区
    val province: String? = null,    //省份
    val city: String? = null,        //城市
    val abbreviation: String? = null,//简称
    val fullName: String? = null,    //全称
    val type: String? = null,        //项目性质
    val winningUnit: String? = null,    //中标单位
    val issign: String? = null,        //合同签订
    val thisYearOderAmount: Double? = null,
    //今年在手订单金额
    val groupSubcontractingPowerServiceAmount: Double? = null,//集团转包电力服务金额
    val seller: String? = null,        //销售员
    val taxRate: Float = 0.toFloat(),        //税率
    val lastMonthProgressProject: String? = null,    //上个月实际工程进度，例5月，XXX
    val outputValue: Double? = null,    //应产生产值
    val income: Double? = null,        //应产生收入
    val internalLaborCosts: Double? = null,    //内部人工成本
    val internalMaterialEquipmentCosts: Double? = null,    //材料设备成本
    val salesExpense: Double? = null,    //销售费用
    val outsourcingCosts: String? = null,    //外包成本
    val grossProfit: Float = 0.toFloat(),        //毛利
    val grossMargin: Float = 0.toFloat(),        //毛利率
    val confirmationProjectProgress: String? = null,        //工程量确认单项目进度
    val bookValue: Double? = null,        //账面产值
    val bookIncome: Double? = null,        //账面收入
    val remarks: String? = null            //备注

): Serializable {

}
