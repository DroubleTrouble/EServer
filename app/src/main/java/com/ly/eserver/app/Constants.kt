package com.ly.eserver.app

import android.Manifest

/**
 * Created by zengwendi on 2017/6/12.
 */

interface Constants {
    companion object {
        /**
         * 网络请求状态
         */
        val STATE_UNKNOWN = 1002
        val STATE_LOADING = 1003
        val STATE_ERROR = 1004
        val STATE_EMPTY = 1005
        val STATE_SUCCESS = 1006

        val QINIU_API = "http://ov6m80kxi.bkt.clouddn.com/"

        //支持规约类型
        /**
         * DLT645-2007
         */
        val DLT_645_2007: Long = 0
        /**
         * DLT645-1997
         */
        val DLT_645_1997 : Long = 1
        /**
         * CJ/T 188-2004
         */
        val CJ_T_188_2004 = 2
        /**
         * 蓝牙
         */
        val QUERY_BLUETOOTH = 0
        /**
         * 红外
         */
        val QUERY_INFRARED = 1
        /**
         * 串口
         */
        val QUERY_SERIAL_PORT = 2

        /**
         * 水表
         */
        val WATER_METER = 0x0002001
        /**
         * 气表
         */
        val GAS_METER = 0x0002002
        /**
         * 热表
         */
        val HOT_METER = 0x0002003
        /**
         * 是否需登录后返回
         */
        val IS_NEED_BACK = "isNeedBack"
        /**
         * 有线水表
         */
        val WIRED_METER = "有线水表"
        /**
         * 无线水表
         */
        val WIRELESS_METER = "无线水表"
        /**
         * 901F
         */
        val MODE_901F = "901F"
        /**
         * 1F90
         */
        val MODE_1F90 = "1F90"
    }
}

