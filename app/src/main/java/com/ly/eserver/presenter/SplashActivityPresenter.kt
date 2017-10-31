package com.ly.eserver.presenter

import com.ly.eserver.presenter.base.BaseView

/**
 * Created by Max on 2017/10/25.
 */
interface SplashActivityPresenter {
    interface View : BaseView<Any?>

    interface Presenter {
        fun download()
        fun getVersion()
    }
}