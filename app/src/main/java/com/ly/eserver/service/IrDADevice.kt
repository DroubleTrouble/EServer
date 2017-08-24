package com.ly.eserver.service


import android.os.Handler
import android.os.Message
import android.util.Log


import com.ly.eserver.listener.IrListener

import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.experimental.and

/**
 * 红外设备

 * @author Xuqn
 */
class IrDADevice private constructor() {

    private val IOCTRL_PMU_GPS_ON = 0x11
    private val IOCTRL_PMU_IRDA_ON = 0x12

    private var m_bReadThreadRun = false
    var m_handler: Handler? = null

    private var writeIsIdle = true
    private val writeExecutor: ExecutorService

    private val readbuffer = ByteArray(1024)
    private var readLen = 0
    private var sendLen = 0

    private var lastReadData = byteArrayOf()//最近一次读取数据

    private var lastSendTime: Long = 0

    init {
        m_handler = MyHandler()
        writeExecutor = Executors.newSingleThreadExecutor()
    }

    private class MyHandler : Handler() {

        override fun handleMessage(msg: Message) {
            //复制消息
            val rev = Message.obtain()
            rev.copyFrom(msg)
            //发送数据
            if (irListener != null) irListener!!.onIrReceived(rev)
        }
    }


    /**
     * 采用默认设置打开红外设备
     */
    fun IrDA_Open() {

        IrDA_Open(RateType.RATE_1200, DataBitType.DATA_8, ParityType.ODD, StopBitType.STOP_1)
    }

    /**
     * 打开红外设备
     */
    fun IrDA_Open(rate: Int, parity: Int) {
        IrDA_Open(RateType.fromValue(rate), DataBitType.DATA_8, ParityType.fromValue(parity), StopBitType.STOP_1)
    }

    /**
     * 打开红外设备
     */
    fun IrDA_Open(rate: RateType, databit: DataBitType, verify: ParityType, stopbit: StopBitType): Int {
        if (m_bReadThreadRun) {
            return -1
        }
        //打开红外
        val status = open()
        //红外配置
        SetOpt(rate.value, databit.value, verify.value, stopbit.value.toInt())
        //红外使能
        SetPowerState(IOCTRL_PMU_IRDA_ON)
        //打开读取线程
        synchronized(READ_LOCK) {
            Thread(ReadSerialRunnable(), "ReadSerialRunnable").start()
        }
        return status
    }

    /**
     * 关闭红外设备
     */
    fun IrDA_Close() {
        if (!m_bReadThreadRun) return
        //清空数据
        sendLen = 0
        m_bReadThreadRun = false
        //关闭红外
        close()
        //GPS使能(红外使能关闭)
        SetPowerState(IOCTRL_PMU_GPS_ON)
    }

    /**
     * 变更设置

     * @param rate
     * *
     * @param databit
     * *
     * @param verify
     * *
     * @param stopbit
     */
    fun IrDA_Set(rate: RateType, databit: DataBitType, verify: ParityType, stopbit: StopBitType) {
        if (!m_bReadThreadRun) return
        //红外配置
        SetOpt(rate.value, databit.value, verify.value, stopbit.value.toInt())
        //红外使能
        SetPowerState(IOCTRL_PMU_IRDA_ON)
    }

    fun IrDA_Set(rate: Int, parity: Char) {
        if (!m_bReadThreadRun) return
        //红外配置
        SetOpt(rate, DataBitType.DATA_8.value, parity, StopBitType.STOP_1.value.toInt())
        //红外使能
        SetPowerState(IOCTRL_PMU_IRDA_ON)
    }

    /**
     * 读取最后一次读取的有效数据

     * @return
     */
    fun IrDA_Read_Last(): ByteArray {
        return lastReadData
    }

    /**
     * 写数据

     * @param DataBuffer
     * *
     * @return
     */
    fun IrDA_Write(DataBuffer: ByteArray): Boolean {
        if (sendLen != 0 && System.currentTimeMillis() - lastSendTime < 500 && !writeIsIdle)
            return false
        writeIsIdle = false
        lastSendTime = System.currentTimeMillis()
        sendLen = DataBuffer.size
        //		write(DataBuffer);
        writeExecutor.execute(WriteThread(DataBuffer))
        return true
    }

    /**
     * 异步写线程,防止write方法阻塞主线程

     * @author Xuqn
     */
    private inner class WriteThread(var writeBuffer: ByteArray?) : Runnable {

        override fun run() {
            try {
                if (writeBuffer == null) return
                write(writeBuffer!!)
            } finally {
                writeIsIdle = true
            }
        }

    }

    /**
     * 设置串口监听器

     * @param listener
     */
    fun setIrListener(listener: IrListener) {
        irListener = listener
    }

    // 读串口线程的接口实现
    private inner class ReadSerialRunnable : Runnable {

        override fun run() {
            if (m_bReadThreadRun) return //防止线程重复打开
            synchronized(READ_LOCK) {
                m_bReadThreadRun = true
                while (m_bReadThreadRun) {
                    var nBytesRead = 0
                    try {
                        Thread.sleep(50)
                        nBytesRead = read(readbuffer)
                        if (nBytesRead > 0) {
                            //有数据接收
                            val m_msgRecv = Message.obtain()
                            val m_msgSend = Message.obtain()
                            m_msgRecv.what = IRDA_MESSAGE
                            m_msgSend.what = IRDA_MESSAGE
                            m_msgRecv.arg1 = IRDA_READ
                            m_msgSend.arg1 = IRDA_WRITE
                            readLen = nBytesRead - sendLen
                            //							Log.i("IR_Rev", toHexString(readbuffer, nBytesRead));
                            //接收数据处理
                            if (readLen > 0) {
                                //数据结构:发送数据+接收数据或接收数据
                                m_msgRecv.obj = ByteArray(readLen)
                                System.arraycopy(readbuffer, sendLen, m_msgRecv.obj, 0, readLen)
                                lastReadData = m_msgRecv.obj as ByteArray
                            }
                            //发送数据处理
                            if (sendLen > 0) {
                                if (nBytesRead >= sendLen) {
                                    m_msgSend.obj = ByteArray(sendLen)
                                    sendLen = 0
                                } else if (nBytesRead < sendLen) {
                                    m_msgSend.obj = ByteArray(nBytesRead)
                                    sendLen -= nBytesRead
                                }
                                System.arraycopy(readbuffer, 0, m_msgSend.obj, 0, (m_msgSend.obj as ByteArray).size)
                                m_handler!!.sendMessage(m_msgSend)
                            }
                            if (m_handler != null && readLen > 0) {
                                m_handler!!.sendMessage(m_msgRecv)
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }


    }

    private external fun open(): Int

    private external fun SetOpt(buadrate: Int, data: Int, parity: Char, stop: Int)

    private external fun close()

    /**
     * 发送数据,在主线程调用会阻塞主线程

     * @param data
     * *
     * @return
     */
    external fun write(data: ByteArray): Int

    private external fun read(data: ByteArray, len: Int, timeout: Int): Int // 读扫描数据，int

    private external fun read(data: ByteArray): Int

    private external fun SetPowerState(controlcode: Int): Int

    private external fun PowerEnable(stete: Boolean): Int //红外电源状态。true 红外开启

    private external fun ScanState(stete: Boolean): Int //扫描状态，true ：扫描头出红光

    /**
     * 检查红外设备是否可用

     * @return
     */
    fun checkIrDevice(): Boolean {
        val status = open()
        //打开红外状态小于0则红外设备不可用
        return status >= 0
    }

    companion object {

        val READ_LOCK = Any()

        val IRDA_MESSAGE = 9000
        val IRDA_READ = 9001
        val IRDA_WRITE = 9002

        private var irListener: IrListener? = null

        /**
         * 获取红外设备

         * @return
         */
        val irDaDevice = IrDADevice()


        fun toHexString(byteArray: ByteArray?, size: Int): String {
            if (byteArray == null || byteArray.size < 1)
                throw IllegalArgumentException(
                        "this byteArray must not be null or empty")
            val hexString = StringBuilder(2 * size)
            for (i in 0..size - 1) {
                if (byteArray[i] and 0xff.toByte() < 0x10)
                //
                    hexString.append("0")
                hexString.append(Integer.toHexString(0xFF and byteArray[i].toInt()))
                if (i != byteArray.size - 1)
                    hexString.append(" ")
            }
            return hexString.toString().toUpperCase(Locale.CHINA)
        }

        init {
            try {
                System.loadLibrary("infrared")
            } catch (e: UnsatisfiedLinkError) {
                Log.e("JNI", "WARNING: Could not load libinfrared.so")
            }

        }
    }

}
