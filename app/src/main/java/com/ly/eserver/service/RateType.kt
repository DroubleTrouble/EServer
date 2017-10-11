package com.ly.eserver.service

/**
 * 波特率

 * @author Qing
 */
enum class RateType
/**
 * 波特率

 * @param value
 */
(val value: Int) {
    RATE_600(600),
    RATE_1200(1200),
    RATE_2400(2400),
    RATE_4800(4800),
    RATE_9600(9600),
    RATE_19200(19200);


    companion object {

        fun fromValue(value: Int): RateType {
            when (value) {
                600 -> return RATE_600
                1200 -> return RATE_1200
                2400 -> return RATE_2400
                4800 -> return RATE_4800
                9600 -> return RATE_9600
                19200 -> return RATE_19200
                else -> return RATE_1200
            }
        }
    }
}
