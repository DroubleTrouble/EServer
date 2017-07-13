package com.ly.eserver.ui.activity.main

import android.content.Context
import android.content.SharedPreferences
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.bean.UserBean
import com.ly.eserver.presenter.LoginActivityPresenter
import com.ly.eserver.presenter.impl.LoginActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity

/**
 * 登录页面
 * 密码还未加密，保存用户数据是否确认使用SharedPreferences，还需要考虑
 * Created by Max on 2017/7/6.
 */
class LoginActivity(override val layoutId: Int = R.layout.activity_login) : BaseActivity<LoginActivityPresenterImpl>(),
        LoginActivityPresenter.View , AnkoLogger {
    //打开Preferences，名称为setting，如果存在则打开它，否则创建新的Preferences
    lateinit var settings : SharedPreferences
    var userBean : UserBean = UserBean()

    override fun refreshView(mData: UserBean) {
        //使用SharedPreferences 保存工号、密码
        settings = getSharedPreferences("User", Context.MODE_PRIVATE)
        //让setting处于编辑状态
        val editor : SharedPreferences.Editor = settings.edit()
        editor.putString("userid",mData.userid)
        editor.putString("password",mData.password)
        editor.putString("username",mData.username)
        editor.putString("projectid",mData.projectid)
        editor.putString("phone",mData.phone)
        editor.putString("department",mData.department)
        editor.apply()
        startActivity<MainActivity>()
    }

    override fun initData() {
        mPresenter = LoginActivityPresenterImpl()
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        settings = getSharedPreferences("User",Context.MODE_PRIVATE)
        if (settings.getString("userid",null) != null) {
            et_login_userid.setText(settings.getString("userid", null))
            et_login_password.setText(settings.getString("password", null))
        }
    }

    override fun initView() {
        //忘记密码
//        tv_login_forgetpwd.setOnClickListener{ view ->
//
//        }
        //发送认证请求，成功则转到mainactivity
        iv_login_next.setOnClickListener {
            userBean.userid = et_login_userid.text.toString()
            userBean.password = et_login_password.text.toString()
            info(userBean.userid+" "+userBean.password)
            mPresenter.login(userBean)
        }

    }


}