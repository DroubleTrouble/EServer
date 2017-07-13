package com.ly.eserver.presenter

import com.ly.eserver.bean.DataBean
import com.ly.eserver.bean.UserBean
import com.ly.eserver.presenter.base.BaseView

/**
 * Created by Max on 2017/7/6.
 */
interface LoginActivityPresenter {
    interface View : BaseView<UserBean>

    interface Presenter {
        fun login(userBean: UserBean)
    }
}