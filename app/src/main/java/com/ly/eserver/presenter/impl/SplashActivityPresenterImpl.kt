package com.ly.eserver.presenter.impl

import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.SplashActivityPresenter
import shinetechzz.com.vcleaders.presenter.base.BasePresenter
import org.jetbrains.anko.*
/**
 * Created by Max on 2017/10/25.
 */
class SplashActivityPresenterImpl : BasePresenter<SplashActivityPresenter.View>(),
        SplashActivityPresenter.Presenter, AnkoLogger{
    override fun getVersion() {
        val listener = object : HttpOnNextListener<Any>() {
            override fun onNext(t: Any?) {
                mView!!.refreshView(t)
            }
        }
        invoke(ApiManager.instence.service.getVersion(), Callback(listener))    }

    override fun download() {
        val listener = object : HttpOnNextListener<Any>() {
            override fun onNext(t: Any?) {
                mView!!.refreshView(t)
            }
        }
        invoke(ApiManager.instence.service.download(), Callback(listener))
    }
}