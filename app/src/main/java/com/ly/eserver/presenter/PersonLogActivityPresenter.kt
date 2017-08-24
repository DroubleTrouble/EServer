package com.ly.eserver.presenter

import com.ly.eserver.bean.PersonlogBean
import com.ly.eserver.presenter.base.BaseView

/**
 * Created by Max on 2017/8/1.
 */
interface PersonLogActivityPresenter{
    interface View : BaseView<Any?>

    interface Presenter {
        fun insertPersonlog(personlogBean: PersonlogBean)
    }
}