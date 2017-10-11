package com.ly.eserver.service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log

import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.app.Constants
import com.ly.eserver.listener.BluetoothListener
import com.ly.eserver.ui.activity.BlueToothActivity
import com.ly.eserver.util.BluetoothSet
import com.ly.eserver.ui.widgets.ProgressDialog

import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.text.MessageFormat
import java.util.UUID
import org.jetbrains.anko.*

@SuppressLint("Registered")
@Suppress("UNREACHABLE_CODE")
/**
 * 蓝牙服务
 * @author Xuqn
 */
class BluetoothService : Service() ,AnkoLogger {

    private var mContext: Context? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val mHandler: BluetoothHandler

    /**
     * 获取暂停前工作状态
     * @return
     */
    var preStatus: BluetoothStatus? = null
        private set//暂停前状态
    /**
     * 获取服务状态
     * @return
     */
    var status: BluetoothStatus? = null
        private set
    /**
     * 获取连接端口
     * @return
     */
    var socket: BluetoothSocket? = null
        private set

    private var mBinder: IBinder? = null
    private var mListener: BluetoothListener? = null

    private val mFilter: IntentFilter
    private val mBluetoothBroadcast: BluetoothBroadcast

    private var mSharedPreferences: SharedPreferences? = null
    private var defaultName: String? = null
    private var defaultAddr: String? = null

    init {
        status = BluetoothStatus.STOP
        preStatus = BluetoothStatus.STOP
        mHandler = BluetoothHandler(this)
        mFilter = IntentFilter()
        mBluetoothBroadcast = BluetoothBroadcast()
        mFilter.addAction(BluetoothDevice.ACTION_FOUND)//设备搜索事件
        mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)//设备状态变更事件
        mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)//搜索结束事件
    }

    override fun onBind(intent: Intent): IBinder? {
        if (mBinder == null) mBinder = BluetoothBinder()
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()

        mContext = applicationContext
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            ToastUtils.showShort("本设备不支持蓝牙设备")
        }

        //加载默认设备
        mSharedPreferences = mContext!!.getSharedPreferences(BluetoothSet.SET_NAME, Context.MODE_PRIVATE)
        defaultName = mSharedPreferences!!.getString(BlueToothActivity.DEFAULT_BLUETOOTH_NAME, "")
        defaultAddr = mSharedPreferences!!.getString(BlueToothActivity.DEFAULT_BLUETOOTH_ADDR, "")

        registerReceiver(mBluetoothBroadcast, mFilter)

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBluetoothBroadcast)
        stop()
    }


    /**
     * 蓝牙暂停
     */
    fun pause() {
        if (status == BluetoothStatus.PAUSE
                || status == BluetoothStatus.STARTING
                || status == BluetoothStatus.STOPING) {
            return
        }
        //暂停蓝牙操作
        preStatus = status
        status = BluetoothStatus.PAUSE
        //关闭蓝牙搜索
        mHandler.removeMessages(BLUETOOTH_SEARCH)
        if (mBluetoothAdapter != null && mBluetoothAdapter!!.isDiscovering) {
            mBluetoothAdapter!!.cancelDiscovery()
        }
    }

    /**
     * 蓝牙恢复
     */
    fun resume() {
        //重新加载
        defaultName = mSharedPreferences!!.getString(BlueToothActivity.DEFAULT_BLUETOOTH_NAME, "")
        defaultAddr = mSharedPreferences!!.getString(BlueToothActivity.DEFAULT_BLUETOOTH_ADDR, "")
        if (mSharedPreferences!!.getInt(BluetoothSet.QUERY_DEVICE, -1) != Constants.QUERY_BLUETOOTH) {
            status = preStatus
            return
        }
        if (socket == null) {
            //未建立蓝牙连接,则发起设备搜索
            status = BluetoothStatus.STOP
            mHandler.removeMessages(BLUETOOTH_SEARCH)
            mHandler.sendEmptyMessage(BLUETOOTH_SEARCH)
        } else if (status == BluetoothStatus.PAUSE) {
            //恢复
            when (preStatus) {
                BluetoothService.BluetoothStatus.START -> status = preStatus
                BluetoothService.BluetoothStatus.STOP -> {
                    status = preStatus
                    start(socket)
                }
                else -> {
                }
            }
        } else if (status == BluetoothStatus.STOP) {
            //若蓝牙异常关闭则重新打开
            start(socket)
        }
    }

    /**
     * 蓝牙停止
     */
    fun stop() {
        //关闭蓝牙
        if (status == BluetoothStatus.PAUSE) {
            //若在暂停状态下关闭连接则更新暂停前状态
            if (preStatus != BluetoothStatus.START) return
            preStatus = BluetoothStatus.STOPING
        } else if (status == BluetoothStatus.START) {
            status = BluetoothStatus.STOPING
        } else {
            return
        }
        try {
            if (socket != null) {
                socket!!.close()
                socket = null
            }
        } catch (e: IOException) {
        }

    }

    /**
     * 蓝牙启动

     * @param socket
     * *
     * @return
     */
    fun start(socket: BluetoothSocket?): Boolean {
        if (socket == null) {
            //搜索默认蓝牙设备用于建立连接
            when (status) {
                BluetoothService.BluetoothStatus.STOP -> {
                    //只有在停止状态可以进行此操作
                    mHandler.removeMessages(BLUETOOTH_SEARCH)
                    mHandler.sendEmptyMessage(BLUETOOTH_SEARCH)
                    return true
                }
                else -> return false
            }
        } else {
            //使用指定端口建立蓝牙连接
            //判断当前蓝牙连接状态
            when (status) {
                BluetoothService.BluetoothStatus.PAUSE -> {
                    //暂停状态下不建立连接,但是保留预连接端口
                    if (preStatus == BluetoothStatus.STOP || preStatus == BluetoothStatus.STOPING) {
                        this.socket = socket
                        return true
                    } else {
                        info("当前连接未关闭,不可重复建立连接")
                        return false
                    }
                    if (socket.remoteDevice.name == this.socket!!.remoteDevice.name && socket.remoteDevice.address == this.socket!!.remoteDevice.address) {
                        //若为相同蓝牙设备且已建立连接则不重复建立
                        return true
                    } else {
                        //不同设备则建立连接失败
                        info( "当前连接未关闭,不可重复建立连接")
                        return false
                    }
                    //若为打开中则执行失败
                    return false
                }
                BluetoothService.BluetoothStatus.START -> {
                    if (socket.remoteDevice.name == this.socket!!.remoteDevice.name && socket.remoteDevice.address == this.socket!!.remoteDevice.address) {
                        return true
                    } else {
                        info("当前连接未关闭,不可重复建立连接")
                        return false
                    }
                    return false
                }
                BluetoothService.BluetoothStatus.STARTING -> return false
                else -> {
                }
            }
            this.socket = socket
            //若蓝牙为断开状态则建立蓝牙连接
            if (status == BluetoothStatus.STOP) {
                //异步建立连接
                Thread(ConnectThread()).start()
                return true
            } else return status == BluetoothStatus.PAUSE && preStatus == BluetoothStatus.STOP
        }
    }

    /**
     * 打开等待对话框
     * @param title
     * *
     * @param msg
     */
    fun showProgressDialog( title : String, msg : String) {
        val dialog : ProgressDialog? = ProgressDialog.getInstance()
        dialog!!.setTitle(title)
        dialog.setMsg(msg)
        dialog.show()
    }

    /**
     * 关闭等待对话框
     */
        fun hideProgressDialog() {
        val dialog : ProgressDialog? = ProgressDialog.getInstance()
        if (dialog != null) {
                dialog.dismiss()
            }
        }

    /**
     * 连接建立线程
     * @author Xuqn
     */
    private inner class ConnectThread : Runnable {

        override fun run() {
            try {
                //建立连接
                if (status == BluetoothStatus.PAUSE) {
                    preStatus = BluetoothStatus.STARTING
                } else {
                    status = BluetoothStatus.STARTING
                }
                socket!!.connect()
                //启动接收
                val bluetooth = Thread(BluetoothThread(socket!!.inputStream))
                bluetooth.name = "BluetootReceiveThread"
                bluetooth.start()
                if (status == BluetoothStatus.PAUSE) {
                    preStatus = BluetoothStatus.START
                } else {
                    status = BluetoothStatus.START
                }
                info( "蓝牙连接成功")
                hideProgressDialog()
                mHandler.sendMessage(mHandler.obtainMessage(BLUETOOTH_CONNECT, true))
            } catch (e: IOException) {
                info( "蓝牙连接失败", e)
                if (status == BluetoothStatus.PAUSE) {
                    preStatus = BluetoothStatus.STOP
                } else {
                    status = BluetoothStatus.STOP
                }
                mHandler.sendMessage(mHandler.obtainMessage(BLUETOOTH_CONNECT, false))
                hideProgressDialog()
            }
        }
    }

    /**
     * 蓝牙发送

     * @param data
     * *
     * @return
     */
    fun write(data: ByteArray): Boolean {
        Log.e("bluetoothService socket", socket.toString())
        Log.e("bluetoothService status", status.toString())

        if (socket != null && status == BluetoothStatus.START) {
            try {
                socket!!.outputStream.write(data)
                socket!!.outputStream.flush()
                return true
            } catch (e: IOException) {
                Log.e("bluetoothService ",e.toString())
                return false
            }
        }
        return false
    }

    /**
     * 设置蓝牙接收

     * @param listener
     */
    fun setDeviceListener(listener: BluetoothListener) {
        mListener = listener
    }

    /**
     * 蓝牙服务Binder

     * @author Xuqn
     */
    inner class BluetoothBinder : Binder() {
        val service: BluetoothService
            get() = this@BluetoothService
    }

    /**
     * 蓝牙接收线程

     * @author Xuqn
     */
    private inner class BluetoothThread(private val mIntputStream: InputStream) : Runnable {

        override fun run() {
            var msg: Message
            while (true) {
                if (status == BluetoothStatus.STOP || status == BluetoothStatus.STOPING) {
                    //普通状态下关闭蓝牙
                    break
                }
                if (status == BluetoothStatus.PAUSE && (preStatus == BluetoothStatus.STOP || preStatus == BluetoothStatus.STOPING)) {
                    //暂停状态下关闭蓝牙
                    break
                }
                val len: Int
                try {
                    val buffer = ByteArray(1024)
                    len = mIntputStream.read(buffer)
                    if (len > 0) {
                        val rev = ByteArray(len)
                        System.arraycopy(buffer, 0, rev, 0, len)
                        if (status != BluetoothStatus.PAUSE) {
                            //蓝牙服务未暂停,则发送数据
                            msg = mHandler.obtainMessage(BLUETOOTH_RECEIVE)
                            msg.obj = rev
                            mHandler.sendMessage(msg)
                        }
                    }
                } catch (e: IOException) {
                    //蓝牙关闭
                    msg = mHandler.obtainMessage(BLUETOOTH_STATUS)
                    if (status != BluetoothStatus.PAUSE && status != BluetoothStatus.STOPING || status == BluetoothStatus.PAUSE && preStatus != BluetoothStatus.STOPING) {
                        //异常关闭
                        msg.obj = e
                    }
                    if (status == BluetoothStatus.PAUSE) {
                        //若在暂停状态下关闭连接则更新暂停前状态
                        preStatus = BluetoothStatus.STOP
                    } else {
                        status = BluetoothStatus.STOP
                    }
                    mHandler.sendMessage(msg)
                }

            }

        }

    }

    private class BluetoothHandler(service: BluetoothService) : Handler(),AnkoLogger {
        internal var mService: WeakReference<BluetoothService>

        init {
            mService = WeakReference(service)
        }

        override fun handleMessage(msg: Message) {
            val service = mService.get() ?: return
            when (msg.what) {
                BLUETOOTH_CONNECT -> {
                    //蓝牙连接状态
                    val status = if (msg.obj != null) msg.obj as Boolean else false
                    if (status) {
                        ToastUtils.showShort("蓝牙连接成功")
                    } else {
                        ToastUtils.showShort("蓝牙连接失败")
                        //发起下次连接尝试(重新搜索)
                        service.socket = null
                        service.status = BluetoothStatus.STOP
                        service.mHandler.sendEmptyMessageDelayed(BLUETOOTH_SEARCH, (BLUETOOTH_SEARCH_INTERVAL / 5).toLong())
                    }
                }
                BLUETOOTH_SEARCH ->
                    //蓝牙设备搜索
                    if (service.mBluetoothAdapter != null) {
                        if (service.mBluetoothAdapter!!.isEnabled) {
                            if (!service.mBluetoothAdapter!!.isDiscovering) {
                                //蓝牙可用且蓝牙开启
                                if (!service.mBluetoothAdapter!!.startDiscovery()) {
                                    info("蓝牙设备搜索启动失败")
                                } else {
                                    //发起下次搜索
                                    service.mHandler.sendEmptyMessageDelayed(BLUETOOTH_SEARCH, BLUETOOTH_SEARCH_INTERVAL.toLong())
                                }
                            } else {
                                //搜索状态忙,发起下次搜索
                                service.mHandler.sendEmptyMessageDelayed(BLUETOOTH_SEARCH, (BLUETOOTH_SEARCH_INTERVAL / 5).toLong())
                            }
                        }
                    }
                BLUETOOTH_RECEIVE -> {
                    //蓝牙设备数据接收
                    val rev = service.mHandler.obtainMessage()
                    rev.copyFrom(msg)
                    if (service.mListener != null)
                        service.mListener!!.onBluetoothReceived(rev)
                }
                BLUETOOTH_STATUS -> {
                    //蓝牙设备状态
                    val tmpStatus: BluetoothStatus
                    if (service.status == BluetoothStatus.PAUSE) {
                        tmpStatus = service.preStatus!!
                    } else {
                        tmpStatus = service.status!!
                    }
                    when (tmpStatus) {
                        BluetoothService.BluetoothStatus.START -> info("蓝牙启动(恢复)")
                        BluetoothService.BluetoothStatus.PAUSE -> info("蓝牙暂停")
                        BluetoothService.BluetoothStatus.STOP ->
                            //蓝牙关闭
                            if (msg.obj != null) {
                                //包含异常信息,异常关闭
                                info("蓝牙异常关闭", msg.obj as Throwable)
                            } else {
                                info("蓝牙关闭")
                            }
                        BluetoothService.BluetoothStatus.STARTING -> info("蓝牙启动中")
                        BluetoothService.BluetoothStatus.STOPING -> info("蓝牙停止中")

                    }
                }
            }
        }
    }

    inner class BluetoothBroadcast : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (status != BluetoothStatus.PAUSE) {
                val action = intent.action
                if (action == BluetoothDevice.ACTION_FOUND) {
                    when (status) {
                        BluetoothService.BluetoothStatus.START, BluetoothService.BluetoothStatus.STARTING, BluetoothService.BluetoothStatus.STOPING ->
                            //此状态下不进行蓝牙设备操作
                            return
                        else -> {
                        }
                    }
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    info( MessageFormat.format("Found: {0}:{1}", device.name, device.address))
                    if (device.name == null) return
                    if (device.name == defaultName && device.address == defaultAddr) {
                        try {
                            //获取蓝牙连接
                            //							BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SERVICE_UUID);
                            val m = device.javaClass.getMethod("createRfcommSocket", *arrayOf<Class<Int>?>(elements = Int::class.javaPrimitiveType))
                            val socket = m.invoke(device, 1) as BluetoothSocket
                            //关闭搜索
                            mBluetoothAdapter!!.cancelDiscovery()
                            //主动开启连接
                            start(socket)
                            mHandler.removeMessages(BLUETOOTH_SEARCH)
                        } catch (e: Exception) {
                            info("蓝牙端口创建失败", e)
                            mHandler.sendEmptyMessageDelayed(BLUETOOTH_SEARCH, (BLUETOOTH_SEARCH_INTERVAL / 5).toLong())
                        }

                    }
                } else if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                    mHandler.removeMessages(BLUETOOTH_SEARCH)
                    when (status) {
                        BluetoothService.BluetoothStatus.STOP -> {
                            ToastUtils.showShort("找不到默认蓝牙外设或默认蓝牙外设未设置")
                            mHandler.sendEmptyMessageDelayed(BLUETOOTH_SEARCH, BLUETOOTH_SEARCH_INTERVAL.toLong())
                        }
                        else -> {
                        }
                    }
                } else if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val status = intent.extras.getInt(BluetoothAdapter.EXTRA_STATE, -1)
                    Log.i(action, MessageFormat.format("蓝牙状态字:{0}", status))
                }
            }
        }

    }


    /**
     * 蓝牙状态

     * @author Xuqn
     */
    enum class BluetoothStatus {
        //开启
        START,
        //开启中
        STARTING,
        //暂停
        PAUSE,
        //停止
        STOP,
        //关闭中
        STOPING
    }

    companion object {
        val SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        val BLUETOOTH_RECEIVE = 1100//蓝牙接收
        val BLUETOOTH_STATUS = 1101//蓝牙状态
        val BLUETOOTH_SEARCH = 1102//蓝牙搜索
        val BLUETOOTH_CONNECT = 1103//蓝牙连接
        val BLUETOOTH_SEARCH_INTERVAL = 20 * 1000
    }
}
