package com.ly.eserver.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Message
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.amap.api.location.AMapLocation
import com.amap.api.navi.model.AmapCarLocation
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.OperlogBean
import com.ly.eserver.db.dao.OperlogDao
import com.ly.eserver.listener.DeviceListener
import com.ly.eserver.presenter.ReadDataActivityPresenter
import com.ly.eserver.presenter.impl.ReadDataActivityPresenterImp
import com.ly.eserver.service.DeviceControl
import com.ly.eserver.ui.activity.base.BaseActivity
import com.ly.eserver.ui.widgets.ProgressDialog
import com.ly.eserver.util.ParseUtil
import com.ly.eserver.util.QueryRequest
import com.ly.eserver.util.StringUtil
import kotlinx.android.synthetic.main.activity_readdata.*
import kotlinx.android.synthetic.main.item_titlebar.*
import org.jetbrains.anko.info
import java.nio.ByteBuffer
import java.util.*

/**
 * 抄读数据页面
 * Created by Max on 2017/8/7.
 */
class ReadDataActivity(override val layoutId: Int = R.layout.activity_readdata) : BaseActivity<ReadDataActivityPresenterImp>(),
        ReadDataActivityPresenter.View, DeviceListener {
    lateinit var mContext : Context
    //定义一个String类型的List数组作为数据源
    var dataList: ArrayList<String> = ArrayList<String>()
    //定义一个ArrayAdapter适配器作为spinner的数据适配器
    lateinit var adapter: ArrayAdapter<String>
    var mDeviceControl: DeviceControl? = null//抄读设备控制模块
    protected var revBuffer = ByteBuffer.allocate(256)//防止帧数据长度超出
    val ON_READ_REQUEST = -2000//抄读请求
    val ON_PARSE_RECEIVED = -3000//解析帧请求
    val ON_REQUEST_TIMEOUT = -3001//抄读超时
    val ON_DIALOG_DELAY_DISMISS = -4000

    val MAX_REQUEST_COUNT = 2//单帧最大请求次数
    val queryRequest: QueryRequest = QueryRequest()
    val REQUEST_TIMEOUT_VALUE: Long = 15 * 1000//请求超时时间
    var operlogDao : OperlogDao = OperlogDao(this@ReadDataActivity)
    var operlog : OperlogBean = OperlogBean()
    var amapLocation : AMapLocation? = null

    override fun refreshView(mData: Any?) {
        ToastUtils.showShort("发送到服务器成功！")
    }

    override fun onHandlerReceive(msg: Message) {
        var data: ByteArray? = queryRequest.getQueryRequest(ed_readdata_tableAddress, sp_readdata_tableType)
        when (msg.what) {
            ON_READ_REQUEST -> {
                if (data != null) {
                    mDeviceControl!!.write(data)
                    showProgressDialog()
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(ON_REQUEST_TIMEOUT, 0, 0, data), REQUEST_TIMEOUT_VALUE)
                }
            }
            //抄读请求超时
            ON_REQUEST_TIMEOUT -> {
                var count = msg.arg1
                count++
                if (count < MAX_REQUEST_COUNT) {
                    data = msg.obj as ByteArray
                    mDeviceControl!!.write(data)
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(ON_REQUEST_TIMEOUT, count, 0, data), REQUEST_TIMEOUT_VALUE)
                } else {
                    ToastUtils.showShort("抄读请求超时")
                    hideProgressDialog()
                }
            }
            ON_PARSE_RECEIVED -> { //回复帧处理
                val result = java.util.ArrayList<String>()
                //获取返回报文帧
                data = msg.obj as ByteArray
                //解析报文
                try {
                    //数据标识
                    //获取数据标识
                    when (sp_readdata_tableType.selectedItemId) {
                        Constants.DLT_645_1997 ->
                            //解析数据
                            result.addAll(queryRequest.parse97Data(data))
                        Constants.DLT_645_2007 ->
                            //解析数据
                            result.addAll(queryRequest.parse07Data(data))
                        else -> {
                        }
                    }
                    ToastUtils.showShort("请求发送成功")
//                    mHandler.removeMessages(ON_REQUEST_TIMEOUT)
//                    progressDialog.dismiss()
                } catch (ex: Throwable) {
                    ToastUtils.showShort("数据解析失败")
                    result.add("数据解析失败")
                } finally {
                    hideProgressDialog()
                    mHandler.removeMessages(ON_REQUEST_TIMEOUT)
                    tv_readdata_readResult.text = result.toString().substring(1, result.toString().length-1)
                    operlog.tableAddress = ed_readdata_tableAddress.text.toString()
                    operlog.result = result.toString().substring(1, result.toString().length-1)
                    operlog.isfinish = true
                    operlogDao.saveOperlog(operlog)
                }
            }
            ON_DIALOG_DELAY_DISMISS -> {//
                hideProgressDialog()
            }
        }

    }

    /**
     * 显示等待对话框
     */
    protected fun showProgressDialog() {
        var dialog = ProgressDialog(mContext, "抄读中", "请稍候...")
//        if (dialog == null) {
//            dialog = ProgressDialog(mContext, "抄读中", "请稍候...")
//        } else {
//            dialog.setTitle("抄读中")
//            dialog.setMsg("请稍候...")
//        }
        dialog.setOnCancelListener(progressCancelListener)
        if (!dialog.isShowing && !isFinishing)
            dialog.show()

    }

    /**
     * 抄读等待框监听
     */
    var progressCancelListener: DialogInterface.OnCancelListener = DialogInterface.OnCancelListener {
        revBuffer.clear()
        if (mHandler.hasMessages(ON_REQUEST_TIMEOUT)) {
            ToastUtils.showShort("请求取消")
        }
        mHandler.removeMessages(ON_REQUEST_TIMEOUT)
        mHandler.removeMessages(ON_DIALOG_DELAY_DISMISS)
    }
    /**
     * 隐藏等待对话框
     */
    protected fun hideProgressDialog() {
        val dialog = ProgressDialog.getInstance()
        dialog?.dismiss()
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {

    }

    override fun onDeviceReceiver(rev: Message?): Boolean {
        info("onDeviceReceiver:" + StringUtil.bufferToHex(rev!!.obj as ByteArray?))
        if (rev.obj is ByteArray) {
            var response: ByteArray? = null
            //根据当前选择的表计类型获取数据集
            response = ParseUtil.checkDLT645Receive(revBuffer, rev.obj as ByteArray?)

            if (response == null) {
                return false
            }
            //保存接收数据
//            DbUtil.addLogInfo(LogInfo.LOG_TYPE.REV,StringUtil.bufferToHex(response));
            //处理通过,进行帧处理
            mHandler.sendMessage(mHandler.obtainMessage(ON_PARSE_RECEIVED, response))
        }
        return false

    }

    override fun initData() {
        mPresenter = ReadDataActivityPresenterImp()
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        tv_titlebar_title.text = "抄读"
        ll_titlebar_close.visibility = LinearLayout.GONE
        if (intent.extras.get("location").toString() != "") {
            amapLocation = intent.extras.get("location") as AMapLocation
            operlog.address = amapLocation!!.description
            operlog.location = amapLocation!!.latitude.toString() + "/" + amapLocation!!.longitude.toString()
        }
        operlog.userid = KotlinApplication.useridApp
        operlog.projectid = KotlinApplication.projectidApp

    }

    override fun initView() {
        mContext = this@ReadDataActivity

        ll_titlebar_back.setOnClickListener {
            finish()
        }
        dataList.add("协议645/07")
        dataList.add("协议645/97")
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dataList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_readdata_tableType.adapter = adapter

        bt_readdata_startreaddata.setOnClickListener {
            operlog.time = Date(System.currentTimeMillis())//获取当前时间
            operlog.type = sp_readdata_tableType.selectedItem.toString()
            tv_readdata_readResult.text = ""
//            DeviceControl.instance.changeRateParityStop(RateType.RATE_2400, ParityType.EVEN,StopBitType.STOP_1)
            mHandler.sendEmptyMessage(ON_READ_REQUEST)
        }
        val list  = operlogDao.queryOperlog()
        if (list != null) {
            for (item in list) {
                info("list------------"+item.toString())
                mPresenter.insertOperlog(item)
                item.issended = true
                operlogDao.saveOperlog(item)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mContext = this@ReadDataActivity
        // 恢复蓝牙启动状态
        DeviceControl.instance.setBluetoothHandler(KotlinApplication.bind!!)
        if (DeviceControl.instance.mBluetooth != null) {
            DeviceControl.instance.getBluetoothandler().service.resume()
        }
        //启用设备
        if (mDeviceControl == null)
            mDeviceControl = DeviceControl.instance
        mDeviceControl!!.toggle(true)
        mDeviceControl!!.registerDeviceListener(this)
    }

    override fun onPause() {
        super.onPause()
        //停用设备
        if (mDeviceControl == null)
            mDeviceControl = DeviceControl.instance
        mDeviceControl!!.toggle(false)
        mDeviceControl!!.unregisterDeviceListener(this)

    }

}