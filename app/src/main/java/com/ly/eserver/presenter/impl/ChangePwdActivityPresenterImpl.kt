package com.ly.eserver.presenter.impl

import com.ly.eserver.bean.UserBean
import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.ChangePwdActivityPresenter
import org.jetbrains.anko.*
import shinetechzz.com.vcleaders.presenter.base.BasePresenter

/**
 * Created by Max on 2017/7/12.
 */
class ChangePwdActivityPresenterImpl : BasePresenter<ChangePwdActivityPresenter.View>(),
        ChangePwdActivityPresenter.Presenter,AnkoLogger{
    override fun changePwd(userBean: UserBean) {
        val listener = object : HttpOnNextListener<Any>() {
            override fun onNext(t: Any?) {
                mView!!.refreshView(t)
            }
        }
        invoke(ApiManager.instence.service.changePwd(userBean), Callback(listener))
    }
}