package com.ly.eserver.util

import com.ly.eserver.app.Constants
import com.ly.eserver.service.ParityType
import java.util.ArrayList

/**
 * 蓝牙内容设置
 * Created by Max on 2017/8/1.
 */

class BluetoothSet {
    inner class SetInfo {
        var setName: String? = null
        var setComment: String? = null
        var setType: Int = 0
    }

    companion object {
        val SET_NAME = "bluetooth_set"//设置文件名称

        val CHECK_SET = 0//勾选设置
        val LIST_SET = 1//列表或输入框设置

        val BLUETOOTH_SET = "蓝牙"
        val BLUETOOTH_DEVICE = "蓝牙设备"
        val QUERY_DEVICE = "设备选择"
        val QUERY_RATE = "波特率"
        val QUERY_PARITY = "校验"

        /**
         * 获取设置内容

         * @return
         */
        val setInfoList: List<SetInfo>
            get() {
                val setInfoList = ArrayList<SetInfo>()
                val lisInfo = BluetoothSet()
                var info = lisInfo.SetInfo()
                info.setName = BLUETOOTH_SET
                info.setComment = "蓝牙开关"
                info.setType = CHECK_SET
                setInfoList.add(info)

                info = lisInfo.SetInfo()
                info.setName = BLUETOOTH_DEVICE
                info.setComment = "搜索与设置蓝牙设备"
                info.setType = LIST_SET
                setInfoList.add(info)

                info = lisInfo.SetInfo()
                info.setName = QUERY_DEVICE
                info.setComment = "抄读设备选择"
                info.setType = LIST_SET
                setInfoList.add(info)

                info = lisInfo.SetInfo()
                info.setName = QUERY_RATE
                info.setComment = "抄读设备通信波特率"
                info.setType = LIST_SET
                setInfoList.add(info)

                info = lisInfo.SetInfo()
                info.setName = QUERY_PARITY
                info.setComment = "抄读设备通信校验方式"
                info.setType = LIST_SET
                setInfoList.add(info)

                return setInfoList
            }

        /**
         * 获取设备名称

         * @param device
         * *
         * @return
         */
        fun getQueryDevice(device: Int): String {
            when (device) {
                Constants.QUERY_BLUETOOTH -> return "蓝牙"

                Constants.QUERY_INFRARED -> return "红外"

                Constants.QUERY_SERIAL_PORT -> return "串口"
                else -> return "无"
            }
        }

        /**
         * 获取校验类型名称

         * @param parity
         * *
         * @return
         */
        fun getParityType(parity: ParityType): String {
            when (parity) {
                ParityType.ODD -> return "奇校验"
                ParityType.EVEN -> return "偶校验"
                else -> return "无校验"
            }
        }
    }
}


