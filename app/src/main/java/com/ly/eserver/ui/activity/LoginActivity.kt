package com.ly.eserver.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Message
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.dao.UserDao
import com.ly.eserver.presenter.LoginActivityPresenter
import com.ly.eserver.presenter.impl.LoginActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import com.ly.eserver.ui.activity.main.MainActivity
import com.ly.eserver.util.SharedPreferencesUtil
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionNo
import com.yanzhenjie.permission.PermissionYes
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*


/**
 * 登录页面
 * 密码还未加密，保存用户数据是否确认使用SharedPreferences，还需要考虑
 * Created by Max on 2017/7/6.
 */
class LoginActivity(override val layoutId: Int = R.layout.activity_login) : BaseActivity<LoginActivityPresenterImpl>(),
        LoginActivityPresenter.View {
    override fun onHandlerReceive(msg: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
    }

    var userBean : UserBean = UserBean()
    override fun refreshView(mData: UserBean) {
//        保存到数据库中
        val userDao : UserDao = UserDao(this)
//        info("LoginActivity  "+ mData.toString())
        userDao.saveUser(mData)
        KotlinApplication.useridApp = mData.userid!!
        startActivity<MainActivity>()
    }

    override fun initData() {
        mPresenter = LoginActivityPresenterImpl()
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        if (SharedPreferencesUtil.getInt(this, "user","userid") != 0 )
            et_login_userid.setText(SharedPreferencesUtil.getInt(this, "user","userid").toString())
        et_login_password.setText(SharedPreferencesUtil.getString(this, "user","password"))
    }

    override fun initView() {
        getPermission()
        //忘记密码
        tv_login_forgetpwd.setOnClickListener{
            startActivity<RetrievePwdActivity>("userid" to et_login_userid.text.toString().toInt())
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
    /**
     * 安卓6.0后动态获取权限
     */
    fun getPermission(){
        //动态获取权限
        if (AndPermission.hasPermission(this, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS,Manifest.permission.CAMERA)) {
//            initMap()
        } else {
            // 请求用户授权。
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS,Manifest.permission.CAMERA)
                    .send()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // 只需要调用这一句，第一个参数是当前Acitivity/Fragment，回调方法写在当前Activity/Framgent。
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    // 成功回调的方法，用注解即可，里面的数字是请求时的requestCode。
    @PermissionYes(100)
    private fun getYes(grantedPermissions: List<String>) {
        // TODO 申请权限成功。
//        initMap()
        info("申请权限成功")
    }

    // 失败回调的方法，用注解即可，里面的数字是请求时的requestCode。
    @PermissionNo(100)
    private fun getNo(deniedPermissions: List<String>) {
        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第一种：用默认的提示语。
            AndPermission.defaultSettingDialog(this, 1).show()
        }
        info("失败")

    }

}