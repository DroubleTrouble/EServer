package com.ly.eserver.presenter.impl

import com.ly.eserver.bean.GankIoDataBean
import com.ly.eserver.http.ApiManager
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpOnNextListener
import com.ly.eserver.presenter.MainActivityPresenter
import shinetechzz.com.vcleaders.presenter.base.BasePresenter

/**
 * Created by zengwendi on 2017/6/12.
 */
class MainActivityPresenterImpl : BasePresenter<MainActivityPresenter.View>(), com.ly.eserver.presenter.MainActivityPresenter.Presenter {
    override fun fetchData(page: Int, pre_page: Int) {
        val listener = object : HttpOnNextListener<List<GankIoDataBean.ResultBean>>() {
            override fun onNext(t: Any) {
                mView!!.refreshView(t as List<com.ly.eserver.bean.GankIoDataBean.ResultBean>)
            }
        }
        invoke(ApiManager.instence.service.getData("Android", page, pre_page), Callback(listener))
    }

}