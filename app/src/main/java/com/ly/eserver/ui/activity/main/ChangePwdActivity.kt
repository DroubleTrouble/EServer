package com.ly.eserver.ui.activity.main

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


/**
 * Created by Max on 2017/7/12.
 */
class ChangePwdActivity(override val layoutId: Int = R.layout.activity_changepwd) : BaseActivity<ChangePwdActivityPresenterImpl>(),
        ChangePwdActivityPresenter.View {
    var userBean: UserBean = UserBean()
    val userDao : UserDao = UserDao(this)
    lateinit var application: KotlinApplication

    override fun refreshView(mData: UserBean) {
        ToastUtils.showShort("密码修改成功")
    }

    override fun initData() {
        mPresenter = ChangePwdActivityPresenterImpl()
        application = getApplication() as KotlinApplication
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
    }

    override fun initView() {

        ll_changepwd_bg.setOnClickListener { view ->
            finish()
        }
        iv_changepwd_next.setOnClickListener { view ->
            if (et_changepwd_newpwd.text.toString() != et_changepwd_configpwd.text.toString()) {
                ToastUtils.showShort("请确认两次输入的密码是否一致")
            } else {
                //获取工号，密码，验证密码是否正确
                userBean = userDao.queryUser(application.useridApp)!!
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

