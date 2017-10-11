package com.ly.eserver.protocol.exception

import java.text.MessageFormat

/**
 * 报文长度异常
 * @author Xuqn
 */
class PacketLengthException : Exception {

    constructor(packetLenght: Int, curLenght: Int) : super(MessageFormat.format("the packet lenght <{0}> is not correct, the correct lenght is {1}", packetLenght, curLenght))

    constructor(minLenght: Int) : super(MessageFormat.format("the packet lenght is not correct, the min lenght is {0}", minLenght))

    constructor() : super("the packet lenght is not correct")

    companion object {

        private val serialVersionUID = 1L
    }
}
