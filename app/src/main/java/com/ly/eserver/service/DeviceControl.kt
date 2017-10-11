package com.ly.eserver.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Message
import android.util.Log
import org.jetbrains.anko.*

import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.listener.BluetoothListener
import com.ly.eserver.listener.DeviceListener
import com.ly.eserver.listener.IrListener
import com.ly.eserver.listener.SerialListener
import com.ly.eserver.service.BluetoothService.BluetoothBinder
import com.ly.eserver.service.ParityType.ODD
import com.ly.eserver.util.BluetoothSet
import com.ly.eserver.util.ParseUtil
import com.ly.eserver.util.StringUtil

import java.nio.ByteBuffer
import java.text.MessageFormat
import java.util.ArrayList
import java.util.logging.Logger
import kotlin.experimental.or

/**
 * 抄读设备控制模块

 * @author Xuqn
 */
class DeviceControl(private val mContext: Context) : IrListener, SerialListener, BluetoothListener ,AnkoLogger {

    private val deviceListenerList: MutableList<DeviceListener>
    private val deviceSet: SharedPreferences
    private var deviceStatus = false
    var mBluetooth: BluetoothService.BluetoothBinder? = null

    /**
     * 获取蓝牙操作句柄
     * @return
     */
    fun getBluetoothandler(): BluetoothService.BluetoothBinder {
        return mBluetooth!!
    }

    private var blueToothRate = -1
    private var blueToothParity = -1
    private var blueToothStop = -1f

    init {
        deviceSet = mContext.getSharedPreferences(BluetoothSet.SET_NAME, Context.MODE_PRIVATE)
        deviceListenerList = ArrayList<DeviceListener>()
        //设置红外监听
        //        IrDADevice.getIrDaDevice().setIrListener(this);
        //设置串口监听
        //        SerialDevice.getSerialDevice().setSerialListener(this);
    }


    /**
     * 添加设备监听

     * @param listener
     */
    fun registerDeviceListener(listener: DeviceListener) {
        val it = deviceListenerList.iterator()
        while (it.hasNext()) {
            //移除重复项
            if (it.next().javaClass.name == listener.javaClass.name) it.remove()
        }
        deviceListenerList.add(listener)
    }

    /**
     * 取消设备监听

     * @param listener
     */
    fun unregisterDeviceListener(listener: DeviceListener) {
        val it = deviceListenerList.iterator()
        //移除全部同名项
        while (it.hasNext()) {
            if (it.next().javaClass.name == listener.javaClass.name) it.remove()
        }
    }

    /**
     * 设置蓝牙操作句柄

     * @param binder
     */
    fun setBluetoothHandler(binder: BluetoothBinder) {
        mBluetooth = binder
        if (mBluetooth != null) {
            //设置数据监听
            mBluetooth!!.service.setDeviceListener(this)
        }
    }


    /**
     * 切换波特率

     * @param rate
     */
    @Synchronized fun changeRate(rate: RateType) {
        //抄读校验,默认偶校验
        val queryParity = deviceSet.getInt(BluetoothSet.QUERY_PARITY, ParityType.EVEN.value.toInt())
        changeRateAndParity(rate, ParityType.fromValue(queryParity))
    }

    /**
     * 切换校验位

     * @param parity
     */
    @Synchronized fun changeParity(parity: ParityType) {
        //抄读波特率,默认2400
        val queryRate = deviceSet.getInt(BluetoothSet.QUERY_RATE, RateType.RATE_2400.value)
        changeRateAndParity(RateType.fromValue(queryRate), parity)
    }


    /**
     * 切换波特率与校验位

     * @param rate
     * *
     * @param parity
     */
    @Synchronized fun changeRateAndParity(rate: RateType, parity: ParityType) {
        val queryDevice = deviceSet.getInt(BluetoothSet.QUERY_DEVICE, Constants.QUERY_BLUETOOTH)
        when (queryDevice) {
            Constants.QUERY_INFRARED//红外
            -> IrDADevice.irDaDevice.IrDA_Set(rate.value, parity.value)
            Constants.QUERY_BLUETOOTH//蓝牙
            -> {
                blueToothRate = rate.value
                blueToothParity = parity.value.toInt()
            }
        }
    }

    /**
     * 切换波特率与校验位、停止位

     * @param rate
     * *
     * @param parity
     * *
     * @param stop
     */
    @Synchronized fun changeRateParityStop(rate: RateType, parity: ParityType, stop: StopBitType) {
        val queryDevice = deviceSet.getInt(BluetoothSet.QUERY_DEVICE, Constants.QUERY_BLUETOOTH)
        when (queryDevice) {

            Constants.QUERY_BLUETOOTH//蓝牙
            -> {
                blueToothRate = rate.value
                blueToothParity = parity.value.toInt()
                blueToothStop = stop.value
            }
        }
    }

    /**
     * 切换指定设备开关状态

     * @param queryDevice
     * *
     * @param status
     */
    fun toggle(queryDevice: Int, status: Boolean) {
        //抄读波特率,默认2400
        val queryRate = deviceSet.getInt(BluetoothSet.QUERY_RATE, RateType.RATE_2400.value)
        //抄读校验,默认偶校验
        val queryParity = deviceSet.getInt(BluetoothSet.QUERY_PARITY, ParityType.EVEN.value.toInt())
        info("toggle,queryRate   queryParity"+queryRate+ "    "+queryParity)

        when (queryDevice) {
            Constants.QUERY_INFRARED -> if (status) {
                //开红外
                IrDADevice.irDaDevice.IrDA_Open(queryRate, queryParity)
            } else {
                //关红外
                IrDADevice.irDaDevice.IrDA_Close()
            }
            Constants.QUERY_BLUETOOTH -> {
            }
        }//蓝牙不提供关闭方法,只在程序退出时执行关闭操作
    }

    /**
     * 切换开关状态
     */
    fun toggle() {
        toggle(!deviceStatus)
    }

    /**
     * 切换开关状态

     * @param status
     */
    @Synchronized fun toggle(status: Boolean) {
        if (deviceStatus == status) return
        //抄读设备,默认蓝牙
        val queryDevice = deviceSet.getInt(BluetoothSet.QUERY_DEVICE, Constants.QUERY_BLUETOOTH)
        toggle(queryDevice, status)
        deviceStatus = status
    }

    /**
     * 重新加载通信接口设定
     */
    fun reloadSetting() {
        //重置临时设定值
        blueToothRate = -1
        blueToothParity = -1
        blueToothStop = -1f
        //抄读设备,默认蓝牙
        val queryDevice = deviceSet.getInt(BluetoothSet.QUERY_DEVICE, Constants.QUERY_BLUETOOTH)
        //抄读波特率,默认2400
        val queryRate = deviceSet.getInt(BluetoothSet.QUERY_RATE, RateType.RATE_2400.value)
        //抄读校验,默认偶校验
        val queryParity = deviceSet.getInt(BluetoothSet.QUERY_PARITY, ParityType.EVEN.value.toInt())
        when (queryDevice) {
            Constants.QUERY_INFRARED -> IrDADevice.irDaDevice.IrDA_Open(queryRate, queryParity)

            Constants.QUERY_BLUETOOTH -> {
                blueToothRate = -1
                blueToothParity = -1
                blueToothStop = -1f
            }
        }
    }

    /**
     * 发送数据
     * @param data
     */
    fun write(data: ByteArray?) {
        if (data == null)
            return
        val queryDevice = deviceSet.getInt(BluetoothSet.QUERY_DEVICE, Constants.QUERY_BLUETOOTH)
        //发送数据
        write(queryDevice, data)
    }

    /**
     * 发送数据

     * @param device
     * *
     * @param data
     */
    fun write(device: Int, data: ByteArray) {
        var status = false
        info("write,queryRate   queryParity"+blueToothRate+ "    "+blueToothParity + " "+blueToothStop +""+device)
        when (device) {
            Constants.QUERY_INFRARED -> status = IrDADevice.irDaDevice.IrDA_Write(data)

            Constants.QUERY_BLUETOOTH ->  {
                if (KotlinApplication.bind != null){
                    mBluetooth = KotlinApplication.bind
                }
                info("write"+mBluetooth.toString())
                if(mBluetooth != null){
                    //组合蓝牙发送报文
                    if (blueToothRate != -1 && blueToothParity != -1 ) {
                        //临时设定
                        status = mBluetooth!!.service.write(
                                DeviceControl.getBluetoothRequest(
                                        data,
                                        RateType.fromValue(blueToothRate),
                                        ParityType.fromValue(blueToothParity)))
                    } else {
                        //系统设定
                        status = mBluetooth!!.service.write(DeviceControl.getBluetoothRequest(data,
                                RateType.fromValue(deviceSet.getInt(BluetoothSet.QUERY_RATE, -1)),
                                ParityType.fromValue(deviceSet.getInt(BluetoothSet.QUERY_PARITY, -1))))
                    }
                }
            }
        }
        Log.i(TAG, MessageFormat.format("Send:{0}", StringUtil.bufferToHex(data)))
        if (!status)
            ToastUtils.showShort( "数据接口发送失败!"+status)
    }

    override fun onIrReceived(rev: Message) {
        //红外监听
        if (rev.what != IrDADevice.IRDA_MESSAGE) {
            //异常数据
            return
        }
        //判断数据类型
        when (rev.arg1) {
            IrDADevice.IRDA_WRITE -> {
            }
            IrDADevice.IRDA_READ -> {
                Log.i(TAG, MessageFormat.format("Rev:{0}", StringUtil.bufferToHex(rev.obj as ByteArray)))
                val msg = Message.obtain()
                msg.copyFrom(rev)
                for (listener in deviceListenerList) {
                    if (listener != null) {
                        if (listener.onDeviceReceiver(msg)) {
                            //若数据被处理则不继续转发
                            break
                        }
                    }
                }
            }
        }//数据发送,因红外设备接口机制原因,发送数据会由接口端口返回
    }

    override fun onSerialReceived(rev: Message) {

        Log.i(TAG, MessageFormat.format("Rev:{0}", StringUtil.bufferToHex(rev.obj as ByteArray)))
        val msg = Message.obtain()
        msg.copyFrom(rev)
        for (listener in deviceListenerList) {
            if (listener != null) {
                if (listener.onDeviceReceiver(msg)) {
                    //若数据被处理则不继续转发
                    break
                }
            }
        }
    }

    override fun onBluetoothReceived(rev: Message) {
        if (rev.what != BluetoothService.BLUETOOTH_RECEIVE) {
            //异常数据
            return
        }
        Log.i(TAG, MessageFormat.format("Rev:{0}", StringUtil.bufferToHex(rev.obj as ByteArray)))

        val msg = Message.obtain()
        msg.copyFrom(rev)
        for (listener in deviceListenerList) {
            if (listener.onDeviceReceiver(msg)) {
                    //若数据被处理则不继续转发
                    break
            }

        }
    }

    companion object {

        val TAG = "DeviceControl"

        @SuppressLint("StaticFieldLeak")
        private var deviceControl: DeviceControl? = null


        /**
         * 获取设备控制模块实例

         * @return
         */
        val instance: DeviceControl
            get() {
                if (deviceControl == null) {
                    deviceControl = DeviceControl(KotlinApplication.instance().applicationContext)
                }
                return deviceControl as DeviceControl
            }

        /**
         * 获取蓝牙下行请求报文

         * @param request
         * *
         * @param rate
         * *
         * @param parity
         * *
         * @param stop
         * *
         * @param databit
         * *
         * @return
         */
        @JvmOverloads fun getBluetoothRequest(request: ByteArray?, rate: RateType, parity: ParityType, stop: StopBitType = StopBitType.STOP_1, databit: DataBitType = DataBitType.DATA_8): ByteArray {
            var request: ByteArray? = request ?: return ByteArray(0)
            var temp: Byte
            val bf = ByteBuffer.allocate(request!!.size + 17)//68+设置字+68+传输控制字+数据长度(2字节)+数据+校验位+16
            //报文头
            //前导符
            bf.put(byteArrayOf(0xFE.toByte(), 0xFE.toByte(), 0xFE.toByte(), 0xFE.toByte()))
            bf.put(0x68.toByte())
            //波特率设置
            when (rate) {
                RateType.RATE_600 -> temp = 0x01
                RateType.RATE_1200 -> temp = 0x02
                RateType.RATE_2400 -> temp = 0x03
                RateType.RATE_4800 -> temp = 0x04
                RateType.RATE_9600 -> temp = 0x06
                else ->
                    //默认2400
                    temp = 0x03
            }
            bf.put(temp)
            //奇偶校验,停止位,数据位,通道号
            temp = 0x00
            //奇偶校验
            when (parity) {
                ParityType.EVEN -> temp = temp or (1 shl 6).toByte()
                ODD -> temp = temp or (2 shl 6).toByte()
                ParityType.NONE//无校验,不进行处理
                -> {
                }
                else ->
                    //默认偶校验
                    temp = temp or (1 shl 6).toByte()
            }
            //停止位
            when (stop) {
                StopBitType.STOP_1 -> temp = temp or (1 shl 4).toByte()
                StopBitType.STOP_1_5 -> temp = temp or (2 shl 4).toByte()
                StopBitType.STOP_2 -> temp = temp or (1 shl 4).toByte()
                else ->
                    //默认停止位1
                    temp = temp or (1 shl 4).toByte()
            }
            //数据位
            when (databit) {
                DataBitType.DATA_7 -> temp = temp or (1 shl 2).toByte()
                DataBitType.DATA_8 -> temp = temp or (2 shl 2).toByte()
                else ->
                    //默认数据位8
                    temp = temp or (2 shl 2).toByte()
            }
            //通道号
            temp = temp or 0x01
            bf.put(temp)
            //保留字节
            bf.put(byteArrayOf(0x00, 0x00, 0x00, 0x00))
            bf.put(0x68.toByte())
            //控制字
            bf.put(0x00.toByte())
            //长度,高字节在前
            val len = ParseUtil.NUMToHEX(request.size.toLong(), 2)
            bf.put(byteArrayOf(len[1], len[0]))
            //数据
            bf.put(request)
            //校验
            request = bf.array()
            var check: Byte = 0
            for (i in 4..bf.position() - 1) {
                check = (check + request.get(i)).toByte()
            }
            bf.put(check)
            //结束
            bf.put(0x16.toByte())
            Log.i(TAG, "获取蓝牙下行请求报文:" + StringUtil.bufferToHex(bf.array()))
            return bf.array()
        }
    }
}
/**
 * 获取蓝牙下行请求报文

 * @param request
 * *
 * @param rate
 * *
 * @param parity
 * *
 * @return
 */
/**
 * 获取蓝牙下行请求报文

 * @param request
 * *
 * @param rate
 * *
 * @param parity
 * *
 * @param stop
 * *
 * @return
 */
