package com.ly.eserver.presenter.impl

import com.ly.eserver.bean.ProjectBean
import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.MainActivityPresenter
import com.ly.eserver.presenter.ProfileFragmentPresenter
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import shinetechzz.com.vcleaders.presenter.base.BasePresenter

/**
 * Created by Max on 2017/8/17.
 */
class ProfileFragmentPresenterImpl : BasePresenter<ProfileFragmentPresenter.View>(), ProfileFragmentPresenter.Presenter, AnkoLogger {
    override fun findProject(projectid: Int) {
        val listener = object : HttpOnNextListener<ProjectBean>() {
            override fun onNext(t: Any?) {
                info("ProfileFragmentPresenterImpl->onNext")
                mView!!.refreshView(t as ProjectBean)
            }
        }
        info("ProfileFragmentPresenterImpl")
        invoke(ApiManager.instence.service.findProject(projectid), Callback(listener))
    }


}