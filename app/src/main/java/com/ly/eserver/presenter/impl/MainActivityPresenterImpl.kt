package com.ly.eserver.presenter.impl

import com.ly.eserver.bean.CheckBean
import com.ly.eserver.bean.ProjectBean
import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.MainActivityPresenter
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import shinetechzz.com.vcleaders.presenter.base.BasePresenter
import java.util.*

/**
 * MainActivityPresenterImpl
 * Created by zengwendi on 2017/6/12.
 */
class MainActivityPresenterImpl : BasePresenter<MainActivityPresenter.View>(),MainActivityPresenter.Presenter,AnkoLogger {

    override fun findCheckByIdAndTime(checkBean: CheckBean) {
        val listener = object : HttpOnNextListener<Any?>() {
            override fun onNext(t: Any?) {
                info("findCheckByIdAndTime->onNext")
                mView!!.refreshView(t)
            }
        }
        invoke(ApiManager.instence.service.findCheckByIdAndTime(checkBean), Callback(listener))
    }

    override fun insertCheck(checkBean: CheckBean) {
        val listener = object : HttpOnNextListener<CheckBean>() {
            override fun onNext(t: Any?) {
                info("insertCheck->onNext")
                mView!!.refreshView(t)
            }
        }
        invoke(ApiManager.instence.service.insertCheck(checkBean), Callback(listener))
    }

    override fun findProject(projectid: Int) {
        val listener = object : HttpOnNextListener<ProjectBean>() {
            override fun onNext(t: Any?) {
                info("findProject->onNext")
                mView!!.refreshView(t as ProjectBean)
            }
        }
        invoke(ApiManager.instence.service.findProject(projectid), Callback(listener))
    }

}