package com.ly.eserver.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Message
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.dao.UserDao
import com.ly.eserver.presenter.RetrievePwdActivityPresenter
import com.ly.eserver.presenter.impl.RetrievePwdActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionNo
import com.yanzhenjie.permission.PermissionYes
import kotlinx.android.synthetic.main.activity_retrievepwd.*
import org.jetbrains.anko.info


/**
 * 忘记密码页面
 * 初步完成还需测试
 * Created by Max on 2017/7/25.
 */
class RetrievePwdActivity(override val layoutId: Int = R.layout.activity_retrievepwd) : BaseActivity<RetrievePwdActivityPresenterImpl>(),
        RetrievePwdActivityPresenter.View {
    override fun onHandlerReceive(msg: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var userBean: UserBean = UserBean()
    var userDao: UserDao = UserDao(this)
    lateinit var application: KotlinApplication
    var flag: Boolean = false
    override fun refreshView(mData: Any?) {
        ToastUtils.showShort("密码重置成功")
    }

    override fun initData() {
        mPresenter = RetrievePwdActivityPresenterImpl()
        application = getApplication() as KotlinApplication
        getPermission()
    }

    override fun loadData() {
        val userid = intent.extras.getInt("userid")
        userBean = userDao.queryUser(userid)!!
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
    }

    override fun initView() {

        ll_retrieve_bg.setOnClickListener {
            finish()
        }
        bt_retrieve_send.setOnClickListener {
            if (et_retrievepwd_phone.text.toString() != userBean.phone) {
                ToastUtils.showShort("绑定手机输入不正确！")
            } else {
                bt_retrieve_send.startTickWork()
                smssdkinit()
                flag = false
                SMSSDK.getVerificationCode("86", userBean.phone)
            }
        }
        iv_retrievepwd_next.setOnClickListener{
            if (!flag)
                SMSSDK.submitVerificationCode("86", userBean.phone, et_retrievepwd_verifycode.text.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SMSSDK.unregisterAllEventHandler()
    }

    fun smssdkinit() {
        // 创建EventHandler对象
        val eh: EventHandler = object : EventHandler() {
            override fun afterEvent(event: Int, result: Int, data: Any?) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        info("提交验证码成功")
                        userBean.password = et_retrievepwd_newpwd.text.toString()
                        mPresenter.changePwd(userBean)
                        flag = true
                        //提交验证码成功
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        info("获取验证码成功")

                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                        info("返回支持发送验证码的国家列表")
                    }
                } else {
                    (data as Throwable).printStackTrace()
                }
            }

        }
        // 注册监听器
        SMSSDK.registerEventHandler(eh)

    }

    /**
     * 安卓6.0后动态获取权限
     */
    fun getPermission() {
        //动态获取权限
        if (AndPermission.hasPermission(this, Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            info("已获得权限")
        } else {
            // 请求用户授权。
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.READ_CONTACTS,
                            Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                            Manifest.permission.ACCESS_FINE_LOCATION)
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
        info("成功")
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