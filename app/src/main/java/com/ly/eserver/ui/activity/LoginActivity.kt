package com.ly.eserver.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Message
import com.cretin.www.cretinautoupdatelibrary.utils.CretinAutoUpdateUtils
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.dao.UserDao
import com.ly.eserver.presenter.LoginActivityPresenter
import com.ly.eserver.presenter.impl.LoginActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import com.ly.eserver.ui.activity.main.MainActivity
import com.ly.eserver.util.EncryptUtil
import com.ly.eserver.util.SharedPreferencesUtil
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionNo
import com.yanzhenjie.permission.PermissionYes
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity


/**
 * 登录页面
 * Created by Max on 2017/7/6.
 */
class LoginActivity(override val layoutId: Int = R.layout.activity_login) : BaseActivity<LoginActivityPresenterImpl>(),
        LoginActivityPresenter.View {
    var userBean : UserBean = UserBean()
    lateinit var tpassword : String

    override fun onHandlerReceive(msg: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
    }

    override fun refreshView(mData: UserBean) {
//        保存到数据库中
        val userDao : UserDao = UserDao(this)
        info("LoginActivity  "+ mData.password)
        userDao.saveUser(mData)
        KotlinApplication.useridApp = mData.userid!!
        KotlinApplication.projectidApp = mData.projectid!!
        startActivity<MainActivity>()
        finish()
    }

    override fun initData() {
        mPresenter = LoginActivityPresenterImpl()
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        if (SharedPreferencesUtil.getInt(this, "user","userid") != 0 )
            et_login_userid.setText(SharedPreferencesUtil.getInt(this, "user","userid").toString())
        if(SharedPreferencesUtil.getString(this, "user","password") != null){
            tpassword = EncryptUtil.desDecryptText(SharedPreferencesUtil.getString(this, "user","password")!!)
            et_login_password.setText(tpassword)
        }
    }

    override fun initView() {
        getPermission()
        CretinAutoUpdateUtils.getInstance(this).check()
        //忘记密码
        tv_login_forgetpwd.setOnClickListener{
            startActivity<RetrievePwdActivity>("userid" to et_login_userid.text.toString().toInt())
        }
        //发送认证请求，成功则转到mainactivity
        iv_login_next.setOnClickListener {
            userBean.userid = et_login_userid.text.toString().toInt()
            userBean.password = et_login_password.text.toString()
            val strAfterDesDecryptPass = EncryptUtil.desEncryptText(userBean.password!!.toUpperCase())
            info("传递用户名和密码=======>" + userBean.userid + "====>" + userBean.password + "=====>" + strAfterDesDecryptPass)
            //使用SharedPreferences 保存用户信息 保存加密的密码
            SharedPreferencesUtil.putUser(this, userBean.userid!!, strAfterDesDecryptPass)
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