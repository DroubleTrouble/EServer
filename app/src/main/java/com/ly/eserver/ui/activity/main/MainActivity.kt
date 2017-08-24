package com.ly.eserver.ui.activity.main

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Message
import android.view.WindowManager
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.ProjectBean
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.dao.UserDao
import com.ly.eserver.presenter.MainActivityPresenter
import com.ly.eserver.presenter.impl.MainActivityPresenterImpl
import com.ly.eserver.service.LocationService
import com.ly.eserver.ui.activity.BlueToothActivity
import com.ly.eserver.ui.activity.ChangePwdActivity
import com.ly.eserver.ui.activity.ReadDataActivity
import com.ly.eserver.ui.activity.ReimbursementActivity
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.menu_content_home.*
import kotlinx.android.synthetic.main.menu_left_profile.*
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity


/**
 * Created by zengwendi on 2017/6/12.
 */
class MainActivity(override val layoutId: Int = R.layout.activity_main) : BaseActivity<MainActivityPresenterImpl>(),
        MainActivityPresenter.View {
    override fun onHandlerReceive(msg: Message) {
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
    }

    val userDao: UserDao = UserDao(this)
    lateinit var user: UserBean
    lateinit var locationService: LocationService
    lateinit var myListener: BDLocationListener
    lateinit var mBaiduMap: BaiduMap
    var main_BDlocation: BDLocation? = null
    var isFirstLoc: Boolean = true

    override fun refreshView(data: ProjectBean) {
        tv_profile_project.text = data.abbreviation
    }

    override fun initData() {
        mPresenter = MainActivityPresenterImpl()
        SDKInitializer.initialize(getApplicationContext())

    }

    override fun loadData() {
        user = userDao.queryUser(KotlinApplication.useridApp)!!
        info("user!-------------" + user.toString())
        if (user.projectid != 0) {
            mPresenter.findProject(user.projectid!!)
        } else {
            mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
            mLoadingPage!!.showPage()
        }
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
            locationService.stop()

        }
        ll_profile_bluetooth.setOnClickListener {
            startActivity<BlueToothActivity>()
            locationService.stop()

        }
        ll_profile_log.setOnClickListener {
            startActivity<ReimbursementActivity>("location" to main_BDlocation!!.locationDescribe)
            locationService.stop()

        }
        //地图页面
        mBaiduMap = mv_menuContent_map.map
        initMap()
        iv_menuContent_location.setOnClickListener {
            if (mBaiduMap.locationConfiguration.locationMode == MyLocationConfiguration.LocationMode.COMPASS) {
                mBaiduMap.setMyLocationConfiguration(
                        MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null))
                val builder1 = MapStatus.Builder()
                builder1.overlook(0f)
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()))
                info("切换为following模式")

            } else {
                mBaiduMap.uiSettings.setOverlookingGesturesEnabled(false)
                mBaiduMap.setMyLocationConfiguration(
                        MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, null))
                val builder1 = MapStatus.Builder()
                builder1.overlook(0f)
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()))
                info("切换为compass模式")
            }
            locationService.registerListener(myListener)
            locationService.start()
        }
        val bluetooth = BluetoothAdapter.getDefaultAdapter()
//        if (bluetooth.isEnabled) {
//            iv_menuContent_bluetooth.setImageResource(R.drawable.close_bluet)
//        }
        ll_menucontent_startdata.setOnClickListener {
            locationService.stop()
            locationService.unregisterListener(myListener)

            if (!bluetooth.isEnabled) {
                ToastUtils.showShort("蓝牙未开启,请打开蓝牙设备!")
                startActivity<BlueToothActivity>()
            } else {
                if (main_BDlocation != null) {
                    startActivity<ReadDataActivity>("location" to main_BDlocation!!)
                } else {
                    startActivity<ReadDataActivity>("location" to "")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val bluetooth = BluetoothAdapter.getDefaultAdapter()
        if (bluetooth.isEnabled) {
            iv_menuContent_bluetooth.setImageResource(R.drawable.close_bluet)
        } else {
            iv_menuContent_bluetooth.setImageResource(R.drawable.open_bluet)
        }
        locationService.registerListener(myListener)
        locationService.start()
    }

    override fun onPause() {
        super.onPause()
        locationService.unregisterListener(myListener)
        locationService.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationService.unregisterListener(myListener)
        locationService.stop()
    }

    internal fun initMap() {
        val wm1: WindowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int = wm1.getDefaultDisplay().width
        val height: Int = wm1.getDefaultDisplay().height
//        mBaiduMap = mv_menuContent_map.map
        mBaiduMap.isMyLocationEnabled = true
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(MapStatus.Builder().zoom(18f).build()))
        mBaiduMap.setOnMapLoadedCallback(BaiduMap.OnMapLoadedCallback {
            mv_menuContent_map.setZoomControlsPosition(Point(width - 150, height - 500))
        })
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService = LocationService(applicationContext)
        //创建监听对象
        myListener = MyLocationListener()
        //注册监听
        locationService.registerListener(myListener)
        mBaiduMap.setMyLocationConfiguration(
                MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null))

//        val type: Int = getIntent().getIntExtra("from", 0)
//        if (type == 0) {
            locationService.setLocationOption(locationService.defaultLocationClientOption)
//        } else if (type == 1) {
//            locationService.setLocationOption(locationService.option)
//        }
        locationService.start()

    }

    override fun onBackPressed() {
        // super.onBackPressed(); 	不要调用父类的方法
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    //定位当前位置-----------------------------------
    inner class MyLocationListener : BDLocationListener {

        override fun onReceiveLocation(location: BDLocation?) {

            if (location == null) {
                return
            }
            main_BDlocation = location
            val locData = MyLocationData.Builder().accuracy(location.radius).direction(100f)
                    .latitude(location.latitude).longitude(location.longitude).build()
            mBaiduMap.setMyLocationData(locData)
            if (isFirstLoc) {
                isFirstLoc = false
                val ll = LatLng(location.latitude, location.longitude)
                val u = MapStatusUpdateFactory.newLatLng(ll)
                mBaiduMap.animateMapStatus(u)
            }
        }
    }


}