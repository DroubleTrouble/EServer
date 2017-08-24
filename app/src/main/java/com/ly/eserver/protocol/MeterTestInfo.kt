package com.ly.eserver.protocol


import com.ly.eserver.service.ParityType
import com.ly.eserver.service.RateType

class MeterTestInfo {
    /**
     * 测试规约名称
     */
    lateinit var testName: String

    var testProtocol: Int = 0//测试规约
    var testStep: Int = 0//测试步骤
    lateinit var testRate: RateType//波特率
    lateinit var testParity: ParityType//校验
    /**
     * 测试结果项
     */
    var testResult: Array<String?>

    var isSelected: Boolean = false

    init {
        testResult = arrayOfNulls<String>(5)
    }

    fun clone(): MeterTestInfo {
        val testInfo = MeterTestInfo()
        testInfo.testName = testName.toString()

        testInfo.testProtocol = Integer.valueOf(testProtocol)!!
        testInfo.testStep = Integer.valueOf(testStep)!!
        testInfo.testRate = testRate
        testInfo.testParity = testParity

        testInfo.testResult = testResult.clone()
        testInfo.isSelected = java.lang.Boolean.valueOf(isSelected)!!
        return testInfo
    }
}
