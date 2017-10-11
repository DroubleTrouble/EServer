package com.ly.eserver.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Message
import com.bumptech.glide.Glide
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.presenter.BigPictureActivityPresenter
import com.ly.eserver.presenter.impl.BigPictureActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bigpicture.*

/**
 * 显示大图页面
 * Created by Max on 2017/8/15.
 */
class BigPictureActivity (override val layoutId: Int = R.layout.activity_bigpicture) :
        BaseActivity<BigPictureActivityPresenterImpl>(), BigPictureActivityPresenter.View {
    var pathPicture : String = ""
    var REQUEST_CODE : Int = 0
    override fun refreshView(mData: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onHandlerReceive(msg: Message) {
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
    }

    override fun initData() {
        mPresenter = BigPictureActivityPresenterImpl()
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()

    }

    override fun initView() {
        if (intent.extras.get("pathPicture1") != null) {
            pathPicture = intent.extras.get("pathPicture1") as String
            REQUEST_CODE = 1
        }else if (intent.extras.get("pathPicture2") != null){
            pathPicture = intent.extras.get("pathPicture2") as String
            REQUEST_CODE = 2
        }
        Glide.with(this).load("file://"+pathPicture).into(iv_bigpicture_picture)
        ll_bigpicture_delete.setOnClickListener {
            this.setResult(REQUEST_CODE)
            finish()
        }
        ll_bigpicture_back.setOnClickListener {
            this.setResult(3)
            finish()
        }
    }
}