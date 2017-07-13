package com.ly.eserver.ui.activity.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.renderscript.RenderScript
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.bean.DataBean
import com.ly.eserver.bean.ProjectBean
import com.ly.eserver.presenter.MainActivityPresenter
import com.ly.eserver.presenter.impl.MainActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import com.ly.eserver.ui.util.BlurBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_left_profile.*
import org.jetbrains.anko.*

/**
 * Created by zengwendi on 2017/6/12.
 */

class MainActivity(override val layoutId: Int = R.layout.activity_main) : BaseActivity<MainActivityPresenterImpl>(),
        MainActivityPresenter.View {
    lateinit var settings : SharedPreferences
    var projectid : String? = null
    override fun refreshView(data: ProjectBean) {
        tv_profile_project.text = data.abbreviation
    }

    override fun initData() {
        mPresenter = MainActivityPresenterImpl()
        settings = getSharedPreferences("User", Context.MODE_PRIVATE)

    }

    override fun loadData() {
        projectid  = settings.getString("projectid",null)
        if (projectid != null) {
            mPresenter.findProject(projectid!!)
        }else{
            mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
            mLoadingPage!!.showPage()
        }
    }

    override fun initView() {
        //左边profile页面
        tv_profile_username.text = settings.getString("username",null)
        tv_profile_department.text = settings.getString("department",null)
        tv_profile_phone.text = settings.getString("phone",null)
        if (projectid == null){
            tv_profile_project.text = null
        }
        ll_profile_changepwd.setOnClickListener { view ->
            startActivity<ChangePwdActivity>()
//            this.overridePendingTransition(R.anim.base_botton_in,R.anim.base_botton_out)
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed(); 	不要调用父类的方法
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }


}
