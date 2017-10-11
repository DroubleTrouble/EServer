package com.ly.eserver.ui.activity.main

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Message
import android.view.KeyEvent
import android.view.WindowManager
import com.baidu.location.BDLocation
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.CheckBean
import com.ly.eserver.bean.ProjectBean
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.dao.UserDao
import com.ly.eserver.presenter.MainActivityPresenter
import com.ly.eserver.presenter.impl.MainActivityPresenterImpl
import com.ly.eserver.service.LocationHelper
import com.ly.eserver.ui.activity.BlueToothActivity
import com.ly.eserver.ui.activity.ChangePwdActivity
import com.ly.eserver.ui.activity.MenuActivity
import com.ly.eserver.ui.activity.ReimbursementActivity
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.menu_content_home.*
import kotlinx.android.synthetic.main.menu_left_profile.*
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import java.util.*
import android.widget.Toast
import android.view.KeyEvent.KEYCODE_BACK
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.BitmapDescriptor
import com.baidu.mapapi.map.MyLocationConfiguration
import com.baidu.mapapi.map.MyLocationData








/**
 * 主页面
 * 有无法定位的情况。这时操作记录要怎么办？
 * Created by zengwendi on 2017/6/12.
 */
class MainActivity(override val layoutId: Int = R.layout.activity_main) : BaseActivity<MainActivityPresenterImpl>(),
        MainActivityPresenter.View, LocationHelper.LocationCallBack {


    val userDao: UserDao = UserDao(this)
    lateinit var user: UserBean
    var check: CheckBean = CheckBean()
    var main_BDlocation: BDLocation? = null
    lateinit var baiduMap: BaiduMap
    lateinit var helper: LocationHelper


    override fun initData() {
        mPresenter = MainActivityPresenterImpl()
        SDKInitializer.initialize(KotlinApplication.instance())

    }

    override fun loadData() {
        user = userDao.queryUser(KotlinApplication.useridApp)!!
        if (user.projectid != 0) {
            mPresenter.findProject(user.projectid!!)
        }
        check.userid = user.userid!!
        mPresenter.findCheckByIdAndTime(check)
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()

    }

    override fun refreshView(mData: Any?) {

//            tv_profile_project.text = mData.abbreviation
        if (mData is ProjectBean) {
            tv_profile_project.text = mData.abbreviation
            info("mData is ProjectBean")

        } else if (mData is Boolean) {
            tv_profile_check.text = "今日已签到"
        } else {
            ll_profile_checkin.setOnClickListener {
                ToastUtils.showShort("签到成功！")
                tv_profile_check.text = "今日已签到"
                check.userid = user.userid!!
                check.address = main_BDlocation!!.locationDescribe
                check.location = main_BDlocation!!.latitude.toString() + "/" + main_BDlocation!!.longitude.toString()
                check.ischeck = true
                check.time = Date(System.currentTimeMillis())
                mPresenter.insertCheck(check)
            }
        }
    }


    override fun onHandlerReceive(msg: Message) {
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
    }

    override fun initView() {

        //左边profile页面
        tv_profile_username.text = user.username
        tv_profile_department.text = user.department
        tv_profile_phone.text = user.phone
        if (user.projectid == 0) {
            tv_profile_project.text = null
        }
        ll_profile_changepwd.setOnClickListener {
            startActivity<ChangePwdActivity>()

        }
        ll_profile_bluetooth.setOnClickListener {
            startActivity<BlueToothActivity>()

        }
        ll_profile_log.setOnClickListener {
            if (main_BDlocation != null) {
                startActivity<ReimbursementActivity>("location" to main_BDlocation!!.locationDescribe)
            }else{
                startActivity<ReimbursementActivity>("location" to "")
            }
        }
        baiduMap = mv_menuContent_map.map

        //地图页面
        startLocation()

        iv_menuContent_location.setOnClickListener {
            if (baiduMap.locationConfiguration.locationMode == MyLocationConfiguration.LocationMode.COMPASS) {
                baiduMap.setMyLocationConfiguration(
                        MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null))
                val builder1 = MapStatus.Builder()
                builder1.overlook(0f)
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()))
                info("切换为following模式")
            } else {
                baiduMap.uiSettings.setOverlookingGesturesEnabled(false)
                baiduMap.setMyLocationConfiguration(
                        MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, null))
                val builder1 = MapStatus.Builder()
                builder1.overlook(0f)
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()))
                info("切换为compass模式")
            }
            startLocation()
        }


        val bluetooth = BluetoothAdapter.getDefaultAdapter()
        if (bluetooth.isEnabled) {
            iv_menuContent_bluetooth.setImageResource(R.drawable.close_bluet)
        }
        ll_menucontent_startdata.setOnClickListener {


            if (!bluetooth.isEnabled) {
                ToastUtils.showShort("蓝牙未开启,请打开蓝牙设备!")
                startActivity<BlueToothActivity>()
            } else {
                if (main_BDlocation != null) {
                    startActivity<MenuActivity>("location" to main_BDlocation!!)
                } else {
                    startActivity<MenuActivity>("location" to "")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mv_menuContent_map.onResume()
        val bluetooth = BluetoothAdapter.getDefaultAdapter()
        if (iv_menuContent_bluetooth != null) {
            if (bluetooth.isEnabled) {
                iv_menuContent_bluetooth.setImageResource(R.drawable.close_bluet)
            } else {
                iv_menuContent_bluetooth.setImageResource(R.drawable.open_bluet)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        mv_menuContent_map.onPause()
    }

    override fun onDestroy() {
        helper.stop();
        baiduMap.setMyLocationEnabled(false);
        mv_menuContent_map.onDestroy();
        super.onDestroy()

    }

    override fun onBackPressed() {
        // super.onBackPressed(); 	不要调用父类的方法
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    //退出时的时间
    private var mExitTime: Long = 0

    //对返回键进行监听
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() === 0) {

            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this@MainActivity, "再按一次退出应用", Toast.LENGTH_SHORT).show()
            mExitTime = System.currentTimeMillis()
        } else {
            finish()
            System.exit(0)
        }
    }
    //百度地图设置
    fun setBaiduMap() {
        val wm1: WindowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int = wm1.getDefaultDisplay().width
        val height: Int = wm1.getDefaultDisplay().height
//        baiduMap = mv_menuContent_map.map
        baiduMap.isMyLocationEnabled = true
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(MapStatus.Builder().zoom(18f).build()))
        baiduMap.setOnMapLoadedCallback(BaiduMap.OnMapLoadedCallback {
            mv_menuContent_map.setZoomControlsPosition(Point(width - 150, height - 500))
        })
        baiduMap.setMyLocationConfiguration(MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null))
        val locData = MyLocationData.Builder().accuracy(main_BDlocation!!.radius).direction(100f)
                .latitude(main_BDlocation!!.latitude).longitude(main_BDlocation!!.longitude).build()
        baiduMap.setMyLocationData(locData)
        val ll = LatLng(main_BDlocation!!.latitude, main_BDlocation!!.longitude)
        val u = MapStatusUpdateFactory.newLatLng(ll)
        baiduMap.animateMapStatus(u)
    }

    override fun callBack(bdLocation: BDLocation) {
        main_BDlocation = bdLocation
        setBaiduMap()
    }

    //开启定位
    fun startLocation() {
        helper = LocationHelper.instance
        helper.callBack = this
        helper.start();
    }


}