package com.ly.eserver.presenter

import com.ly.eserver.bean.UserBean
import com.ly.eserver.presenter.base.BaseView

/**
 * Created by Max on 2017/7/12.
 */
interface ChangePwdActivityPresenter {
    interface View : BaseView<UserBean>

    interface Presenter {
        fun changePwd(userBean: UserBean)
    }
}