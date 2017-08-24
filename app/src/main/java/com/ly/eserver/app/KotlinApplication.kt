package com.ly.eserver.app

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.multidex.MultiDex
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.ly.eserver.bean.UserBean
import com.ly.eserver.service.BluetoothService
import com.ly.eserver.service.DeviceControl
import com.ly.eserver.util.BluetoothSet
import com.mob.MobApplication

/**
 * Created by zengwendi on 2017/6/12.
 */

class KotlinApplication : MobApplication() {
    companion object {
        private var instance: Application? = null
        var useridApp : Int = 0
        var bind : BluetoothService.BluetoothBinder? = null
        fun instance() = instance!!
    }
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        instance = this
        // 开启蓝牙服务
        val service = Intent(this@KotlinApplication, BluetoothService::class.java)
        bindService(service, bluetoothServiceConn, Context.BIND_AUTO_CREATE)
    }

     override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    /**
     * 蓝牙服务连接
     */
    private val bluetoothServiceConn = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            // 服务启动完毕,绑定蓝牙操作句柄
            DeviceControl.instance.setBluetoothHandler(
                    binder as BluetoothService.BluetoothBinder)
            KotlinApplication.bind = binder
            val service = binder.service

            if (this@KotlinApplication.getSharedPreferences(BluetoothSet.SET_NAME,
                    Context.MODE_PRIVATE).getInt(BluetoothSet.QUERY_DEVICE, -1) == Constants.QUERY_BLUETOOTH) {
                // 若当前模式为蓝牙模式则开启蓝牙
                val bluetooth = BluetoothAdapter.getDefaultAdapter()
                if (bluetooth == null) {
                    ToastUtils.showShort("本设备不支持蓝牙设备!")
                } else {
                    if (bluetooth.isEnabled) {
                        service.start(null)
                    }else{
                        ToastUtils.showShort("蓝牙未开启,请打开蓝牙设备!")
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }

    }
}

