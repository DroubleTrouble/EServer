package com.ly.eserver.presenter.impl

import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.MenuActivityPresenter
import shinetechzz.com.vcleaders.presenter.base.BasePresenter
import org.jetbrains.anko.*
/**
 * Created by Max on 2017/8/24.
 */
class MenuActivityPresenterImpl  : BasePresenter<MenuActivityPresenter.View>(),
        MenuActivityPresenter.Presenter, AnkoLogger {
    override fun getQiniu() {
        val listener = object : HttpOnNextListener<String>() {
            override fun onNext(t: Any?) {
                info("LoginActivityPresenterImpl->onNext")
                mView!!.refreshView(t as String)
            }
        }
        info("LoginActivityPresenterImpl")
        invoke(ApiManager.instence.service.getQiniuToken(), Callback(listener))
    }
}