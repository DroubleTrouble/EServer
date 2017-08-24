package com.ly.eserver.presenter.impl

import com.ly.eserver.bean.OperlogBean
import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.ReadDataActivityPresenter
import shinetechzz.com.vcleaders.presenter.base.BasePresenter
import org.jetbrains.anko.*
/**
 * Created by Max on 2017/8/7.
 */
class ReadDataActivityPresenterImp : BasePresenter<ReadDataActivityPresenter.View>(),ReadDataActivityPresenter.Presenter,AnkoLogger {
    override fun insertOperlog(operlogBean: OperlogBean) {
        val listener = object : HttpOnNextListener<Any>() {
            override fun onNext(t: Any?) {
                info("LoginActivityPresenterImpl->onNext")
                mView!!.refreshView(t)
            }
        }
        info("LoginActivityPresenterImpl")
        invoke(ApiManager.instence.service.insertOperlog(operlogBean), Callback(listener))
    }
}