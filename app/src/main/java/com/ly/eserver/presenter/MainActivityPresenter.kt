package com.ly.eserver.presenter

import android.support.v4.app.INotificationSideChannel
import com.ly.eserver.bean.DataBean
import com.ly.eserver.bean.GankIoDataBean
import com.ly.eserver.bean.ProjectBean
import com.ly.eserver.presenter.base.BaseView

/**
 * Created by zengwendi on 2017/6/12.
 */

interface MainActivityPresenter {
    interface View : BaseView<ProjectBean>

    interface Presenter {
        fun findProject(projectid: Int)
    }
}


