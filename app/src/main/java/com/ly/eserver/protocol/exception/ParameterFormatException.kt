package com.ly.eserver.protocol.exception

import java.text.MessageFormat

/**
 * 参数格式异常(字符串)
 * @author Xuqn
 */
class ParameterFormatException
/**
 * 当前参数值, 正确格式
 * @param parameter
 * *
 * @param curFormat
 */
(
        /**
         * 获取输入参数
         * @return
         */
        val parameter: String,
        /**
         * 获取正确格式
         * @return
         */
        val curFormat: String) : Exception(MessageFormat.format("{0} is not in the currect format, the currect format is {1}", curFormat, parameter)) {
    companion object {

        private val serialVersionUID = 1L
    }

}
