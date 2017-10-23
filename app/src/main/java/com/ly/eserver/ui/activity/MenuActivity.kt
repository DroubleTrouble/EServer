package com.ly.eserver.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Message
import android.widget.LinearLayout
import com.amap.api.location.AMapLocation
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.presenter.MenuActivityPresenter
import com.ly.eserver.presenter.impl.MenuActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.item_titlebar.*
import org.jetbrains.anko.*
/**
 * 菜单页面，跳转到抄读数据或者提交报告
 * Created by Max on 2017/8/24.
 */
class MenuActivity(override val layoutId: Int = R.layout.activity_menu) : BaseActivity<MenuActivityPresenterImpl>(),
        MenuActivityPresenter.View {
    var amapLocation : AMapLocation? = null
    override fun refreshView(mData: String) {
        if (mData != ""){
            info("MenuActivity = Qiniu" + mData)
            if (amapLocation != null)
                startActivity<DescriptionActivity>("Qiniu" to mData , "location" to amapLocation!!)
            else
                startActivity<DescriptionActivity>("Qiniu" to mData , "location" to "")
        }
    }

    override fun onHandlerReceive(msg: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
        mPresenter = MenuActivityPresenterImpl()
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        tv_titlebar_title.text = "菜单"
        ll_titlebar_back.visibility = LinearLayout.GONE
        if (intent.extras.get("location").toString() != "") {
            amapLocation = intent.extras.get("location") as AMapLocation
        }
    }

    override fun initView() {
        tv_menu_readdata.setOnClickListener {
            if (checkDeviceStatus()) {
                if (amapLocation != null)
                    startActivity<ReadDataActivity>( "location" to amapLocation!!)
                else
                    startActivity<ReadDataActivity>( "location" to "")
            } else {
                ToastUtils.showShort("前往配置蓝牙连接")
                startActivity<BlueToothActivity>()
            }
        }
        tv_menu_pushreport.setOnClickListener {
            mPresenter.getQiniu()
        }
        ll_titlebar_close.setOnClickListener {
            finish()
        }
    }

}