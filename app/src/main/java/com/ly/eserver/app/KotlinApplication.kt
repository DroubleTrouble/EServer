package com.ly.eserver.app

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.ly.eserver.bean.UserBean
import com.mob.MobApplication

/**
 * Created by zengwendi on 2017/6/12.
 */

class KotlinApplication : MobApplication() {
    var useridApp : Int = 0
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)

    }


}

