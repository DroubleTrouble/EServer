package com.ly.eserver.ui.activity.main

import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.dao.UserDao
import com.ly.eserver.presenter.LoginActivityPresenter
import com.ly.eserver.presenter.impl.LoginActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import com.ly.eserver.ui.util.SharedPreferencesUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*


/**
 * 登录页面
 * 密码还未加密，保存用户数据是否确认使用SharedPreferences，还需要考虑
 * Created by Max on 2017/7/6.
 */
class LoginActivity(override val layoutId: Int = R.layout.activity_login) : BaseActivity<LoginActivityPresenterImpl>(),
        LoginActivityPresenter.View {
    var userBean : UserBean = UserBean()
    lateinit var application : KotlinApplication
    override fun refreshView(mData: UserBean) {
//        保存到数据库中
        val userDao : UserDao = UserDao(this)
        info("LoginActivity  "+ mData.toString())
        userDao.saveUser(mData)
        application.useridApp = mData.userid!!
        startActivity<MainActivity>()
    }

    override fun initData() {
        mPresenter = LoginActivityPresenterImpl()
        application = getApplication() as KotlinApplication
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        et_login_userid.setText(SharedPreferencesUtil.getInt(this, "userid").toString())
        et_login_password.setText(SharedPreferencesUtil.getString(this, "password"))
    }

    override fun initView() {
        //忘记密码
        tv_login_forgetpwd.setOnClickListener{ view ->
            startActivity<RetrievePwdActivity>()
        }
        //发送认证请求，成功则转到mainactivity
        iv_login_next.setOnClickListener {
            userBean.userid = et_login_userid.text.toString().toInt()
            userBean.password = et_login_password.text.toString()
            //使用SharedPreferences 保存用户信息
            SharedPreferencesUtil.putUser(this, userBean.userid!!, userBean.password!!)
            mPresenter.login(userBean)
        }
    }
}