package com.ly.eserver.ui.activity.main

import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.dao.UserDao
import com.ly.eserver.presenter.RetrievePwdActivityPresenter
import com.ly.eserver.presenter.impl.RetrievePwdActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_retrievepwd.*
import org.jetbrains.anko.*

/**
 * Created by Max on 2017/7/25.
 */
class RetrievePwdActivity (override val layoutId: Int = R.layout.activity_retrievepwd) : BaseActivity<RetrievePwdActivityPresenterImpl>(),
        RetrievePwdActivityPresenter.View{
    var userBean : UserBean = UserBean()
    var userDao : UserDao = UserDao(this)
    lateinit var application :KotlinApplication
    override fun refreshView(mData: UserBean) {
        ToastUtils.showShort("密码重置成功")
    }

    override fun initData() {
        mPresenter = RetrievePwdActivityPresenterImpl()
        application = getApplication() as KotlinApplication
    }

    override fun loadData() {
        userBean = userDao.queryUser(application.useridApp)!!
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
    }

    override fun initView() {
        ll_retrieve_bg.setOnClickListener { view ->
            finish()
        }
        ll_retrieve_send.setOnClickListener { view ->
            if (et_retrievepwd_phone.text.toString() != userBean.phone){
                ToastUtils.showShort("绑定手机输入不正确！")
            }else{

            }
        }

    }
}