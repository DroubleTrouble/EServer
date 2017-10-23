package com.ly.eserver.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Message
import android.widget.LinearLayout
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.PersonlogBean
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.dao.UserDao
import com.ly.eserver.presenter.ReimbursementActivityPresenter
import com.ly.eserver.presenter.impl.ReimbursementActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_reimbursement.*
import kotlinx.android.synthetic.main.item_titlebar.*
import org.jetbrains.anko.*
/**
 * 报销页面
 * Created by Max on 2017/8/1.
 */
class ReimbursementActivity(override val layoutId: Int = R.layout.activity_reimbursement) :
        BaseActivity<ReimbursementActivityPresenterImpl>(), ReimbursementActivityPresenter.View {
    override fun onHandlerReceive(msg: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val userDao: UserDao = UserDao(this)
    lateinit var user: UserBean
    var personlog: PersonlogBean = PersonlogBean()
    var location: String = ""

    override fun refreshView(mData: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
        mPresenter = ReimbursementActivityPresenterImpl()
    }

    override fun loadData() {
        user = userDao.queryUser(KotlinApplication.useridApp)!!
        location = intent.extras.getString("location")
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        tv_titlebar_title.text = "工作日志"
        ll_titlebar_back.visibility = LinearLayout.GONE
    }

    override fun initView() {
        tv_reimbursement_department.text = user.department
        tv_reimbursement_location.text = location
        tv_reimbursement_next.setOnClickListener {
            personlog.projectid = user.projectid
            personlog.userid = KotlinApplication.useridApp
            personlog.department = user.department
            personlog.area = location    //所在地
            if (ed_reimbursement_accommodation.text.toString().trim() != "")
                personlog.accommodation = ed_reimbursement_accommodation.text.toString().toDouble()  //住宿费
            if (ed_reimbursement_travel.text.toString().trim() != "")
                personlog.travel = ed_reimbursement_travel.text.toString().toDouble()    //车船费
            if (ed_reimbursement_food.text.toString().trim() != "")
                personlog.food = ed_reimbursement_food.text.toString().toDouble()     //伙补费
            if (ed_reimbursement_vehicle.text.toString().trim() != "")
                personlog.vehicle = ed_reimbursement_vehicle.text.toString().toDouble()    //车辆费用
            if (ed_reimbursement_office.text.toString().trim() != "")
                personlog.office = ed_reimbursement_office.text.toString().toDouble()    //办事处费用
            if (ed_reimbursement_material.text.toString().trim() != "")
                personlog.material = ed_reimbursement_material.text.toString().toDouble()    //材料费
            if (ed_reimbursement_lowValueConsumables.text.toString().trim() != "")
                personlog.lowValueConsumables = ed_reimbursement_lowValueConsumables.text.toString().toDouble()     //低值易耗品
            if (ed_reimbursement_courier.text.toString().trim() != "")
                personlog.courier = ed_reimbursement_courier.text.toString().toDouble()     //快递费
            if (ed_reimbursement_hospitality.text.toString().trim() != "")
                personlog.hospitality = ed_reimbursement_hospitality.text.toString().toDouble()     //招待费
            if (ed_reimbursement_administrative.text.toString().trim() != "")
                personlog.administrative = ed_reimbursement_administrative.text.toString().toDouble()     //办公费
            if (ed_reimbursement_welfare.text.toString().trim() != "")
                personlog.welfare = ed_reimbursement_welfare.text.toString().toDouble()     //福利费
            if (ed_reimbursement_others.text.toString().trim() != "")
                personlog.others = ed_reimbursement_others.text.toString().toDouble()    //其它费用

            startActivity<PersonLogActivity>("personlog" to personlog)
            finish()
        }
        ll_titlebar_close.setOnClickListener {
            finish()
        }
    }
}