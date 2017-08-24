package com.ly.eserver.ui.activity

import android.annotation.SuppressLint
import android.widget.LinearLayout
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.presenter.BlueToothActivityPresenter
import com.ly.eserver.presenter.impl.BlueToothActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bluetooth.*
import kotlinx.android.synthetic.main.item_titlebar.*
import org.jetbrains.anko.info
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.*
import android.os.IBinder
import android.os.Message
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.service.BluetoothService
import com.ly.eserver.service.DeviceControl
import com.ly.eserver.service.ParityType
import com.ly.eserver.service.RateType
import com.ly.eserver.util.BluetoothSet
import com.ly.eserver.ui.widgets.ProgressDialog
import com.ly.eserver.ui.widgets.SelectListDialog
import org.jetbrains.anko.startActivity
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.collections.ArrayList


/**
 * 蓝牙设置页面
 * Created by Max on 2017/7/31.
 */
class BlueToothActivity(override val layoutId: Int = R.layout.activity_bluetooth) :
        BaseActivity<BlueToothActivityPresenterImpl>(), BlueToothActivityPresenter.View{


    override fun refreshView(mData: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var rate : String
    lateinit var vertify : String
    lateinit var mBluetoothAdapter: BluetoothAdapter
    private var bluetoothDeviceList: ArrayList<BluetoothDevice> = ArrayList()
    lateinit var mSharedPreferences: SharedPreferences


    companion object {
        val DEFAULT_BLUETOOTH_NAME = "defaultBluetoothName"
        val DEFAULT_BLUETOOTH_ADDR = "defaultBluetoothAddr"

        val BLUETOOTH_DISCOVERY = 1001
        val BLUETOOTH_CHANGE_STATE = 1002

        val BLUETOOTH_DISCOVERY_TIME_OUT : Long = 1000 * 10
        val BLUETOOTH_CHANGE_TIME_OUT : Long = 5 * 1000
    }

    override fun initData() {
        mPresenter = BlueToothActivityPresenterImpl()

    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        tv_titlebar_title.text = "蓝牙设置"
        ll_titlebar_close.visibility = LinearLayout.GONE
    }

    override fun initView() {
        ll_titlebar_back.setOnClickListener {
            finish()
        }
        rg_bluetooth_rate1.setOnCheckedChangeListener { radioGroup, i ->
           when(i){
               R.id.rb_bluetooth_rate28600 ->{
                   if (rb_bluetooth_rate28600.isChecked()){
                       rate = rb_bluetooth_rate28600.text.toString()
                       rg_bluetooth_rate2.clearCheck()
                   }
               }
               R.id.rb_bluetooth_rate11400->{
                   if (rb_bluetooth_rate11400.isChecked()){
                       rate = rb_bluetooth_rate11400.text.toString()
                       rg_bluetooth_rate2.clearCheck()
                   }
               }
           }
       }
        rg_bluetooth_rate2.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.rb_bluetooth_rate9600->{
                    if (rb_bluetooth_rate9600.isChecked()) {
                        rate = rb_bluetooth_rate9600.text.toString()
                        rg_bluetooth_rate1.clearCheck()
                    }
                }
                R.id.rb_bluetooth_rate3600->{
                    if (rb_bluetooth_rate3600.isChecked()) {
                        rate = rb_bluetooth_rate3600.text.toString()
                        rg_bluetooth_rate1.clearCheck()
                    }
                }
            }
        }
        rg_bluetooth_vertify1.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.rb_bluetooth_parity->{
                    if (rb_bluetooth_parity.isChecked()) {
                        rg_bluetooth_vertify2.clearCheck()
                        vertify = rb_bluetooth_parity.text.toString()
                    }
                }
                R.id.rb_bluetooth_crc->{
                    if (rb_bluetooth_crc.isChecked()){
                        rg_bluetooth_vertify2.clearCheck()
                        vertify = rb_bluetooth_crc.text.toString()
                    }
                }
            }
        }
        rg_bluetooth_vertify2.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.rb_bluetooth_redundancy->{
                    if (rb_bluetooth_redundancy.isChecked()) {
                        vertify = rb_bluetooth_redundancy.text.toString()
                        rg_bluetooth_vertify1.clearCheck()
                    }
                }
            }
        }
        loadSpSet()
        //加载蓝牙设备
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var progressDialog: ProgressDialog
        //连接按钮监听事件
        tv_bluetooth_connect.setOnClickListener {

            mSharedPreferences = this.getSharedPreferences(BluetoothSet.SET_NAME, Context.MODE_PRIVATE)
            //加载设置
            if (mSharedPreferences.getInt(BluetoothSet.QUERY_DEVICE, -1) == -1) {
                //无设置,采用默认设置
                mSharedPreferences.edit()
                        .putInt(BluetoothSet.QUERY_DEVICE, Constants.QUERY_BLUETOOTH)//目前只支持蓝牙
                        .putInt(BluetoothSet.QUERY_RATE, rate.toInt())//波特率(默认2400)
                        .putInt(BluetoothSet.QUERY_PARITY, vertify.toInt())//校验(默认偶校验)
                        .putString(DEFAULT_BLUETOOTH_NAME, "")
                        .putString(DEFAULT_BLUETOOTH_ADDR, "")
                        .commit()
            }
            //如果蓝牙未开启则开启
            if (!mBluetoothAdapter.isEnabled){
                mBluetoothAdapter.enable()
                progressDialog = ProgressDialog(this, "打开中", "蓝牙设备打开中...")
                progressDialog.setCancelable(false)
                progressDialog.show()
                mHandler.sendEmptyMessageDelayed(BLUETOOTH_CHANGE_STATE, BLUETOOTH_CHANGE_TIME_OUT)
            }
            mHandler.removeMessages(BLUETOOTH_DISCOVERY)
            if (mBluetoothAdapter == null) {
                ToastUtils.showShort("无可用蓝牙硬件或驱动")
            } else {
                if (!mBluetoothAdapter.isEnabled) {
                    ToastUtils.showShort("蓝牙未开启或正在开启中")
                    return@setOnClickListener
                }
                if (mBluetoothAdapter.startDiscovery()) {
                    bluetoothDeviceList.clear()
                    mHandler.sendEmptyMessageDelayed(BLUETOOTH_DISCOVERY, BLUETOOTH_DISCOVERY_TIME_OUT)
                    val progressDialog = ProgressDialog(this, "搜索中", "蓝牙设备搜索中...")
                    progressDialog.setOnCancelListener {
                        mHandler.removeMessages(BLUETOOTH_DISCOVERY)
                        mBluetoothAdapter.cancelDiscovery()
                        ToastUtils.showShort("搜索蓝牙设备取消")
                    }
                    progressDialog.show()
                } else {
                    ToastUtils.showShort("搜索蓝牙设备失败")
                }
            }
        }
    }


    @SuppressLint("ApplySharedPref")
    override fun onResume() {
        super.onResume()
        // 恢复蓝牙启动状态
        if (DeviceControl.instance.bluetoothandler != null) {
            DeviceControl.instance.bluetoothandler!!.service.resume()
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        if (mBluetoothAdapter != null) {
//            mSharedPreferences.edit()
//                    .putBoolean(BluetoothSet.BLUETOOTH_SET, mBluetoothAdapter.isEnabled())
//                    .commit()
//        } else {
//            mSharedPreferences.edit()
//                    .putBoolean(BluetoothSet.BLUETOOTH_SET, false)
//                    .commit()
//        }
    }

    /**
     * 加载本地设置
     */
    private fun loadSpSet() {
        mSharedPreferences = this.getSharedPreferences(BluetoothSet.SET_NAME, Context.MODE_PRIVATE)
        //加载设置
        if (mSharedPreferences.getInt(BluetoothSet.QUERY_DEVICE, -1) == -1) {
            //无设置,采用默认设置
            mSharedPreferences.edit()
                    .putInt(BluetoothSet.QUERY_DEVICE, Constants.QUERY_BLUETOOTH)//目前只支持蓝牙
                    .putInt(BluetoothSet.QUERY_RATE, RateType.RATE_2400.value)//波特率(默认2400)
                    .putInt(BluetoothSet.QUERY_PARITY, ParityType.EVEN.value.toInt())//校验(默认偶校验)
                    .putString(DEFAULT_BLUETOOTH_NAME, "")
                    .putString(DEFAULT_BLUETOOTH_ADDR, "")
                    .commit()
        }
    }

    override fun onPause() {
        super.onPause()
        if (DeviceControl.instance.bluetoothandler != null) {
            DeviceControl.instance.bluetoothandler!!.service.pause()
        }
        mHandler.removeMessages(BLUETOOTH_DISCOVERY)
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery()
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
        if (intent.action == BluetoothDevice.ACTION_FOUND) {
            //搜索到设备
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            //过滤异常蓝牙设备
            val name = device.name
            if (name == null || name.trim { it <= ' ' } == "") return
            if (!bluetoothDeviceList.contains(device)) {
                bluetoothDeviceList.add(device)
            }
        } else if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            //设备状态变更
            when (intent.extras.getInt(BluetoothAdapter.EXTRA_STATE, -1)) {
                BluetoothAdapter.STATE_OFF -> ToastUtils.showShort("蓝牙设备已关闭")
                BluetoothAdapter.STATE_ON -> ToastUtils.showShort("蓝牙设备已打开")
                else -> return
            }
            mHandler.removeMessages(BLUETOOTH_CHANGE_STATE)
            val progressDialog = ProgressDialog.getInstance()
            progressDialog?.dismiss()
        }
    }

    override fun onHandlerReceive(msg: Message) {
        val progressDialog: ProgressDialog?
        when (msg.what) {
            BLUETOOTH_DISCOVERY -> {
                //蓝牙设备搜索超时
                if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering) {
                    mBluetoothAdapter.cancelDiscovery()
                }
                ToastUtils.showShort("设备搜索完毕")
                progressDialog = ProgressDialog.getInstance()
                progressDialog?.dismiss()
                //获取默认蓝牙设备
                val defaultName = mSharedPreferences.getString(DEFAULT_BLUETOOTH_NAME, "")
                val defaultAddr = mSharedPreferences.getString(DEFAULT_BLUETOOTH_ADDR, "")
                //若蓝牙已连接(无法再次被搜索到),手动添加至蓝牙设备列表
                val handler = DeviceControl.instance.bluetoothandler
                if (handler != null) {
                    val service = handler.service
                    if (service.status !== BluetoothService.BluetoothStatus.START || service.status !== BluetoothService.BluetoothStatus.STARTING) {
                        val socket = service.socket
                        if (socket != null) {
                            var isExist = false
                            for (device in bluetoothDeviceList) {
                                if (device.name == socket.getRemoteDevice().getName() && device.address == socket.getRemoteDevice().getAddress()) {
                                    isExist = true
                                    break
                                }
                            }
                            if (!isExist) {
                                bluetoothDeviceList.add(socket.getRemoteDevice())
                            }
                        }
                    }
                }
                //选择标签
                var selectIndex = -1
                //打开设备列表
                if (!bluetoothDeviceList.isEmpty()) {
                    val devices = arrayOfNulls<String>(bluetoothDeviceList.size)
                    info("````````````size" + bluetoothDeviceList.size)
                    for (i in devices.indices) {
                        val device = bluetoothDeviceList[i]
                        devices[i] = device.name
                        if (device.name == defaultName && device.address == defaultAddr)
                            selectIndex = i
                    }
                    val dialog = SelectListDialog(this, "请选择要连接的设备", devices, selectIndex, true)
                    dialog.setOnSelectedListener(object : SelectListDialog.OnSelectedListener {
                        override fun onNothingSelected() {
                            mSharedPreferences.edit()
                                    .putString(DEFAULT_BLUETOOTH_NAME, "")
                                    .putString(DEFAULT_BLUETOOTH_ADDR, "")
                                    .commit()
                            val handler = DeviceControl.instance.bluetoothandler
                            if (handler != null) {
                                handler!!.service.stop()
                            }
                        }

                        override fun onItemSelected(v: View, index: Int) {
                            if (bluetoothDeviceList == null || bluetoothDeviceList.size == 0 || bluetoothDeviceList.size <= index) {
                                return
                            }
                            //保存蓝牙选择
                            val device = bluetoothDeviceList[index]
                            info("选择device：" + device.name + "=========" + device.address)
                            mSharedPreferences.edit()
                                    .putString(DEFAULT_BLUETOOTH_NAME, device.name)
                                    .putString(DEFAULT_BLUETOOTH_ADDR, device.address)
                                    .commit()
                            //建立蓝牙连接(预连接,设置窗口关闭后后建立真实连接)
                            val handler: BluetoothService.BluetoothBinder = DeviceControl.instance.bluetoothandler!!
                            info("handler" + handler.toString())
                            if (handler != null) {
                                val socket: BluetoothSocket
                                try {
                                    //								socket = device.createRfcommSocketToServiceRecord(BluetoothService.SERVICE_UUID);
                                    val m = device.javaClass.getMethod("createRfcommSocket", *arrayOf<Class<Int>?>(Int::class.javaPrimitiveType))
                                    socket = m.invoke(device, 1) as BluetoothSocket
                                    info("device.bondState = " + device.bondState)
                                    if (device.bondState == BluetoothDevice.BOND_NONE) {
                                        //未绑定,进行绑定操作
                                        val createBondMethod: Method
                                        var boneRequestStatus = false
                                        try {
                                            info("进行绑定操作")
                                            createBondMethod = BluetoothDevice::class.java.getMethod("createBond")
                                            createBondMethod.invoke(device)
                                            boneRequestStatus = true
                                        } catch (e: NoSuchMethodException) {
                                            e.printStackTrace()
                                            info("蓝牙绑定失败!")
                                        } catch (e: IllegalAccessException) {
                                            e.printStackTrace()
                                            info("蓝牙绑定失败!")
                                        } catch (e: InvocationTargetException) {
                                            e.printStackTrace()
                                            info("蓝牙绑定失败!")
                                        } catch (e: IllegalArgumentException) {
                                            e.printStackTrace()
                                            info("蓝牙绑定失败!")
                                        }

                                        if (!boneRequestStatus) {
                                            //绑定请求失败
                                            info("蓝牙绑定失败!")
                                        }
                                    }
                                    when (handler!!.service.status) {
                                        BluetoothService.BluetoothStatus.PAUSE ->
                                            //暂停状态下,查看暂停前状态
                                            when (handler!!.service.preStatus) {
                                                BluetoothService.BluetoothStatus.START, BluetoothService.BluetoothStatus.STARTING ->
                                                    //关闭旧连接
                                                    handler!!.service.stop()
                                                else -> {
                                                }
                                            }
                                        BluetoothService.BluetoothStatus.START, BluetoothService.BluetoothStatus.STARTING ->
                                            //关闭旧连接
                                            handler!!.service.stop()
                                        else -> {
                                        }
                                    }
                                    if (!handler!!.service.start(socket)) {
                                        info("蓝牙通信端口设置失败")
                                    } else {
                                        info("蓝牙通信端口设置成功")
                                    }
                                } catch (e: Exception) {
                                    info("蓝牙通信端口创建失败", e)
                                }

                            }
                        }
                    })
                    dialog.show()
                }
            }
            BLUETOOTH_CHANGE_STATE -> {
                progressDialog = ProgressDialog.getInstance()
                progressDialog?.dismiss()
            }
        }
    }


}