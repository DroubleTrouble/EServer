package com.ly.eserver.ui.activity

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.dao.UserDao
import com.ly.eserver.presenter.ChangePwdActivityPresenter
import com.ly.eserver.presenter.impl.ChangePwdActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_changepwd.*
import org.jetbrains.anko.*
import android.content.Intent
import android.os.Message




/**
 * 密码修改页面
 * Created by Max on 2017/7/12.
 */
class ChangePwdActivity(override val layoutId: Int = R.layout.activity_changepwd) : BaseActivity<ChangePwdActivityPresenterImpl>(),
        ChangePwdActivityPresenter.View {
    override fun onHandlerReceive(msg: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var userBean: UserBean = UserBean()
    val userDao : UserDao = UserDao(this)

    override fun refreshView(mData: Any?) {
        ToastUtils.showShort("密码修改成功")
        val intent = Intent(this@ChangePwdActivity, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun initData() {
        mPresenter = ChangePwdActivityPresenterImpl()
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
    }

    override fun initView() {

        ll_changepwd_bg.setOnClickListener {
            finish()
        }
        iv_changepwd_next.setOnClickListener {
            if (et_changepwd_newpwd.text.toString() != et_changepwd_configpwd.text.toString()) {
                ToastUtils.showShort("请确认两次输入的密码是否一致")
            } else {
                //获取工号，密码，验证密码是否正确
                userBean = userDao.queryUser(KotlinApplication.useridApp)!!
                if (userBean.password != et_changepwd_oldpwd.text.toString()) {
                    ToastUtils.showShort("原密码错误，无法修改")
                } else {
                    userBean.password = et_changepwd_newpwd.text.toString()
                    mPresenter.changePwd(userBean)
                }
            }
        }
    }

}

