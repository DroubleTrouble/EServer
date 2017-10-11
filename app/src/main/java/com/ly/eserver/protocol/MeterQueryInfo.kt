package com.ly.eserver.protocol

import java.io.Serializable
import java.util.ArrayList


class MeterQueryInfo : Serializable, Comparable<MeterQueryInfo> {

    var queryId = -1//抄读ID大类
    var querySubId = -1//抄读ID小类

    var paraIndex = -1//参编参数序号

    var Priority = -1//排序优先级

    lateinit var queryName: String
    var queryDate: String? = null
    lateinit var queryResult: List<String>

    var isSelected = false//是否选中
    var isProgressing = false//是否处理中

    constructor()

    @JvmOverloads constructor(name: String, id: Int, subid: Int, para: Int = -1) {
        queryName = name
        queryId = id
        querySubId = subid
        paraIndex = para
        queryDate = null
        queryResult = ArrayList<String>()
    }

    override fun compareTo(another: MeterQueryInfo): Int {
        if (Priority != another.Priority) {
            if (Priority == -1) return 1
            if (another.Priority == -1) return -1
            return another.Priority - Priority
        }
        return 0
    }

    companion object {
        private const val serialVersionUID = 1L
    }

}
