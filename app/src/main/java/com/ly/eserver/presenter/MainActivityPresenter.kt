package com.ly.eserver.presenter

import com.ly.eserver.bean.GankIoDataBean
import com.ly.eserver.presenter.base.BaseView

/**
 * Created by zengwendi on 2017/6/12.
 */

interface MainActivityPresenter {
    interface View : BaseView<List<GankIoDataBean.ResultBean>>

    interface Presenter {
        fun fetchData(page: Int, pre_page: Int)
    }
}


