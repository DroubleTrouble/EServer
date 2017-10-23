package com.ly.eserver.util

import android.widget.EditText
import android.widget.Spinner
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.app.Constants
import com.ly.eserver.protocol.MeterQueryInfo
import com.ly.eserver.protocol.collector.ahi.Collector_AHI_Util
import com.ly.eserver.protocol.dlt645.DLT645_Parse
import com.ly.eserver.protocol.dlt645.dlt64507.DLT645_2007
import com.ly.eserver.protocol.dlt645.dlt64507.DLT645_2007_Info
import com.ly.eserver.protocol.dlt645.dlt64597.DLT645_1997
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.text.MessageFormat
import java.util.*

/**
 * Created by Max on 2017/8/14.
 */
class QueryRequest :AnkoLogger{
    protected val HAVE_METER_ADDR = false

    /**
     * 功能:获取645表计数据查询请求报文
     * @return 645表计实时数据查询报文
     */
    fun getQueryRequest(meterAddrValue : EditText,meterProtocolValue : Spinner): ByteArray? {
        var value: String
        //获取检测的表计地址
        if (!StringUtil.checkInputNotNull(meterAddrValue)) {
            ToastUtils.showShort("您还未输入表计地址")
            return null
        }
        //获取检测的表计地址
        value = meterAddrValue.text.toString()
        //补零处理
        value = StringUtil.fillWith(value, '0', 12, false)
        //刷新表计地址
        meterAddrValue.setText(value)
        //请求报文
        var request: ByteArray? = null
        //数据标识
        val dataFlag: ByteArray
        //获取请求报文,即请求645中的当前正向有功电能
        when (meterProtocolValue.selectedItemId) {
            Constants.DLT_645_1997//(90 10--->43 C3)
            -> {
                dataFlag = byteArrayOf(0x43.toByte(), 0xC3.toByte())
                request = DLT645_1997.getReadRequest(dataFlag, value)
            }
            Constants.DLT_645_2007//(00 FF 01 00--->33 32 34 33)
            -> {
                dataFlag = byteArrayOf(0x33.toByte(), 0x32.toByte(), 0x34.toByte(), 0x33.toByte())
                request = DLT645_2007.getReadRequest(dataFlag, value)
            }
        }
        return request
    }

    /**
     * 解析07数据

     * @param data
     * *
     * @return
     */
    fun parse07Data(data: ByteArray): List<String> {
        var result :ArrayList<String>? = null
        //获取07表报文数据标识
        val dateNoAddr = DLT645_2007.getDataInfo(data)
        val dataFlag = DLT645_2007.getDataFlag(dateNoAddr.resultData,4)
        val tempDataFlag = byteArrayOf(0x00.toByte(), 0x01.toByte(), 0xFF.toByte(), 0x00.toByte())
//        info("QueryRequest data: "+ StringUtil.bufferToHex(dataFlag))
//        info("QueryRequest data: "+ StringUtil.bufferToHex(tempDataFlag))
        if (Arrays.equals(dataFlag, tempDataFlag)) {
            //获取数据
            val queryInfo = MeterQueryInfo("正向有功电能", 0, 1)
            result = DLT645_2007.parse(queryInfo, dateNoAddr.resultData) as ArrayList<String>

        }
        return result!!
    }

    /**
     * 解析97数据

     * @param data
     * *
     * @return
     */
    fun parse97Data(data: ByteArray): List<String> {
        var result :ArrayList<String>? = null
        //获取07表报文数据标识
        val dateNoAddr = DLT645_1997.getDataInfo(data)
        val dataFlag = DLT645_1997.getDataFlag(dateNoAddr.resultData,2)
        val tempDataFlag = byteArrayOf(0x10.toByte(), 0x90.toByte())
//        info("QueryRequest data: "+ StringUtil.bufferToHex(dataFlag))
//        info("QueryRequest data: "+ StringUtil.bufferToHex(tempDataFlag))
        if (Arrays.equals(dataFlag, tempDataFlag)) {
            //获取数据
            val queryInfo = MeterQueryInfo("正向有功电能", 0, 1)
            result = DLT645_1997.parse(queryInfo, dateNoAddr.resultData) as ArrayList<String>
        }
        return result!!
    }

}