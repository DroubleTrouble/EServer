package com.ly.eserver.protocol.exception

import java.text.MessageFormat

/**
 * 数据类型异常
 * @author Xuqn
 */
class ProtocolDataTypeException(dataType: Int) : Exception(MessageFormat.format("the data type {0} is not correct", dataType)) {
    companion object {

        private val serialVersionUID = 1L
    }
}
