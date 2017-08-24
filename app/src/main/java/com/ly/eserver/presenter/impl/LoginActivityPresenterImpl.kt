package com.ly.eserver.presenter.impl

import com.ly.eserver.bean.DataBean
import com.ly.eserver.bean.UserBean
import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.LoginActivityPresenter
import org.jetbrains.anko.*
import shinetechzz.com.vcleaders.presenter.base.BasePresenter

/**
 * Created by Max on 2017/7/6.
 */
class LoginActivityPresenterImpl :BasePresenter<LoginActivityPresenter.View>(),
        LoginActivityPresenter.Presenter, AnkoLogger {
    override fun login(userBean: UserBean) {
        val listener = object : HttpOnNextListener<UserBean>() {
            override fun onNext(t: Any?) {
                info("LoginActivityPresenterImpl->onNext")
                mView!!.refreshView(t as UserBean)
            }
        }
        info("LoginActivityPresenterImpl")
        invoke(ApiManager.instence.service.login(userBean), Callback(listener))
    }
}