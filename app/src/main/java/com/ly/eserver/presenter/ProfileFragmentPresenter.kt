package com.ly.eserver.presenter

import com.ly.eserver.bean.ProjectBean
import com.ly.eserver.presenter.base.BaseView

/**
 * Created by Max on 2017/8/17.
 */
interface ProfileFragmentPresenter {
    interface View : BaseView<ProjectBean>

    interface Presenter {
        fun findProject(projectid: Int)
    }
}