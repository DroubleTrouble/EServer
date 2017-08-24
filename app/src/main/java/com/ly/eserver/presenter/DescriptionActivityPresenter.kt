package com.ly.eserver.presenter

import com.ly.eserver.bean.DescriptionBean
import com.ly.eserver.presenter.base.BaseView

/**
 * Created by Max on 2017/8/11.
 */
interface DescriptionActivityPresenter {
    interface View : BaseView<Any?>

    interface Presenter {
        fun insertDescription(descriptionBean: DescriptionBean)
    }
}