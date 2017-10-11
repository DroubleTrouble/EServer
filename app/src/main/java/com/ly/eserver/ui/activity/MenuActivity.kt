package com.ly.eserver.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Message
import android.widget.LinearLayout
import com.baidu.location.BDLocation
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
    var main_BDlocation : BDLocation? = null

    override fun refreshView(mData: String) {
        if (mData != ""){
            info("MenuActivity = Qiniu" + mData)
            if (main_BDlocation != null)
                startActivity<DescriptionActivity>("Qiniu" to mData , "location" to main_BDlocation!!)
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
            main_BDlocation = intent.extras.get("location") as BDLocation
        }
    }

    override fun initView() {
        tv_menu_readdata.setOnClickListener {
            if (main_BDlocation != null)
                startActivity<ReadDataActivity>( "location" to main_BDlocation!!)
            else
                startActivity<ReadDataActivity>( "location" to "")
        }
        tv_menu_pushreport.setOnClickListener {
            mPresenter.getQiniu()
        }
    }

}