package com.ly.eserver.presenter.impl

import com.ly.eserver.bean.ProjectBean
import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.MainActivityPresenter
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import shinetechzz.com.vcleaders.presenter.base.BasePresenter

/**
 * Created by zengwendi on 2017/6/12.
 */
class MainActivityPresenterImpl : BasePresenter<MainActivityPresenter.View>(),MainActivityPresenter.Presenter,AnkoLogger {
    override fun findProject(projectid: Int) {
        val listener = object : HttpOnNextListener<ProjectBean>() {
            override fun onNext(t: Any) {
                info("LoginActivityPresenterImpl->onNext")
                mView!!.refreshView(t as ProjectBean)
            }
        }
        info("LoginActivityPresenterImpl")
        invoke(ApiManager.instence.service.findProject(projectid), Callback(listener))
    }


}