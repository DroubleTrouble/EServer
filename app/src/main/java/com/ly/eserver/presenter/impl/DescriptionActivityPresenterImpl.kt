package com.ly.eserver.presenter.impl

import com.ly.eserver.bean.DescriptionBean
import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.DescriptionActivityPresenter
import shinetechzz.com.vcleaders.presenter.base.BasePresenter
import org.jetbrains.anko.*
/**
 * 说明
 * Created by Max on 2017/8/11.
 */
class DescriptionActivityPresenterImpl : BasePresenter<DescriptionActivityPresenter.View>(),DescriptionActivityPresenter.Presenter,AnkoLogger {

    override fun insertDescription(descriptionBean: DescriptionBean) {
        val listener = object : HttpOnNextListener<Any>() {
            override fun onNext(t: Any?) {
                mView!!.refreshView(t)
            }
        }
        invoke(ApiManager.instence.service.insertDescription(descriptionBean), Callback(listener))
    }
}