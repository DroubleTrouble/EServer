package com.ly.eserver.presenter

import com.ly.eserver.presenter.base.BaseView

/**
 * Created by Max on 2017/8/24.
 */
interface MenuActivityPresenter{
    interface View : BaseView<String>

    interface Presenter {
        fun getQiniu()
    }
}