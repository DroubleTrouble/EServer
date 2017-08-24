package com.ly.eserver.protocol.exception

import java.text.MessageFormat

/**
 * 规约信息类别(queryId, querySubId, paraIndex)异常
 * @see .MeterQueryInfo

 * @author Xuqn
 */
class ProtocolIdException(errorType: Int, id: Int)//参数类异常信息
//无错误类型异常信息
//小类异常信息
//大类异常信息
    : Exception(if (errorType != TYPE_ID)
    if (errorType != TYPE_SUB_ID)
        if (errorType != TYPE_PARAMETER_INDEX)
            MessageFormat.format("{0} is not correct parameter index", id)
        else
            MessageFormat.format("{0} is not correct id", id)
    else
        MessageFormat.format("{0} is not correct query id", id)
else
    MessageFormat.format("{0} is not correct query sub id", id)) {
    companion object {

        private val serialVersionUID = 1L

        val TYPE_ID = 0//大类
        val TYPE_SUB_ID = 1//小类
        val TYPE_PARAMETER_INDEX = 2//参数类
    }


}
