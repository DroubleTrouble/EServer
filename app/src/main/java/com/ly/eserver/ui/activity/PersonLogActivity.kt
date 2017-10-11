package com.ly.eserver.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Message
import android.widget.LinearLayout
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.bean.PersonlogBean
import com.ly.eserver.presenter.PersonLogActivityPresenter
import com.ly.eserver.presenter.impl.PersonLogActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_personlog.*
import kotlinx.android.synthetic.main.item_titlebar.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 个人日志页面
 * Created by Max on 2017/8/2.
 */
class PersonLogActivity(override val layoutId: Int = R.layout.activity_personlog) :
        BaseActivity<PersonLogActivityPresenterImpl>(), PersonLogActivityPresenter.View{
    override fun onHandlerReceive(msg: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var personlog :PersonlogBean = PersonlogBean()

    override fun refreshView(mData: Any?) {
        ToastUtils.showShort("提交成功")
    }

    override fun initData() {
        mPresenter = PersonLogActivityPresenterImpl()
    }

    override fun loadData() {
        personlog = intent.extras["personlog"] as PersonlogBean
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        tv_titlebar_title.text = "工作日志"
        ll_titlebar_close.visibility = LinearLayout.GONE
    }

    override fun initView() {
        bt_personlog_commit.setOnClickListener {
            if (et_personlog_content.text.toString().trim() == ""){
                ToastUtils.showShort("工作内容不能为空")
            }else{
                personlog.personlog = et_personlog_content.text.toString()
                personlog.time = Date(System.currentTimeMillis())//获取当前时间
                mPresenter.insertPersonlog(personlog)
            }
        }
        ll_titlebar_back.setOnClickListener {
            finish()
        }
    }

}