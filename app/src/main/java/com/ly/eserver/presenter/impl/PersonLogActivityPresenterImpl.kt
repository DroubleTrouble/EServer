package com.ly.eserver.presenter.impl

import com.ly.eserver.bean.PersonlogBean
import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.PersonLogActivityPresenter
import shinetechzz.com.vcleaders.presenter.base.BasePresenter
import org.jetbrains.anko.*
/**
 * Created by Max on 2017/8/1.
 */
class PersonLogActivityPresenterImpl :BasePresenter<PersonLogActivityPresenter.View>(),PersonLogActivityPresenter.Presenter,AnkoLogger {
    override fun insertPersonlog(personlogBean: PersonlogBean) {
        val listener = object : HttpOnNextListener<Any>() {
            override fun onNext(t: Any?) {
                mView!!.refreshView(t)
            }
        }
        invoke(ApiManager.instence.service.insertPersonlog(personlogBean), Callback(listener))
    }
}