package com.ly.eserver.presenter

import com.ly.eserver.bean.OperlogBean
import com.ly.eserver.presenter.base.BaseView

/**
 * Created by Max on 2017/8/7.
 */
interface ReadDataActivityPresenter {
    interface View : BaseView<Any?>

    interface Presenter {
        fun insertOperlog(operlogBean: OperlogBean)
    }
}