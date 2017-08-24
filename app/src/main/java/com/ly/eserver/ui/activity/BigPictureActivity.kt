package com.ly.eserver.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Message
import com.bumptech.glide.Glide
import com.ly.eserver.R
import com.ly.eserver.presenter.BigPictureActivityPresenter
import com.ly.eserver.presenter.impl.BigPictureActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bigpicture.*
import kotlinx.android.synthetic.main.activity_description.*

/**
 * 显示大图页面
 * Created by Max on 2017/8/15.
 */
class BigPictureActivity (override val layoutId: Int = R.layout.activity_bigpicture) :
        BaseActivity<BigPictureActivityPresenterImpl>(), BigPictureActivityPresenter.View {
    var pathPicture : String = ""

    override fun refreshView(mData: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onHandlerReceive(msg: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
        mPresenter = BigPictureActivityPresenterImpl()
    }

    override fun loadData() {
        pathPicture = intent.extras.getString("pathPicture")
    }

    override fun initView() {
        Glide.with(this).load("file://"+pathPicture).into(iv_bigpicture_picture)
        ll_bigpicture_delete.setOnClickListener {
//            startActivity<>()
        }
    }
}