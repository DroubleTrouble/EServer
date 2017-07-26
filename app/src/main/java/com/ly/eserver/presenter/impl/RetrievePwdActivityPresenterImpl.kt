package com.ly.eserver.presenter.impl

import com.ly.eserver.bean.UserBean
import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.ChangePwdActivityPresenter
import com.ly.eserver.presenter.RetrievePwdActivityPresenter
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import shinetechzz.com.vcleaders.presenter.base.BasePresenter

/**
 * Created by Max on 2017/7/25.
 */
class RetrievePwdActivityPresenterImpl : BasePresenter<RetrievePwdActivityPresenter.View>(),
        RetrievePwdActivityPresenter.Presenter, AnkoLogger {
    override fun changePwd(userBean: UserBean) {
        val listener = object : HttpOnNextListener<UserBean>() {
            override fun onNext(t: Any) {
                mView!!.refreshView(t as UserBean)
            }
        }
        invoke(ApiManager.instence.service.changePwd(userBean), Callback(listener))
    }
}