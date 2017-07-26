package com.ly.eserver.ui.activity.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.view.WindowManager
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MyLocationConfiguration
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.ProjectBean
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.dao.UserDao
import com.ly.eserver.presenter.MainActivityPresenter
import com.ly.eserver.presenter.impl.MainActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import com.ly.eserver.ui.util.map.service.LocationService
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionNo
import com.yanzhenjie.permission.PermissionYes
import kotlinx.android.synthetic.main.menu_content_home.*
import kotlinx.android.synthetic.main.menu_left_profile.*
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity


/**
 * Created by zengwendi on 2017/6/12.
 */
class MainActivity(override val layoutId: Int = R.layout.activity_main) : BaseActivity<MainActivityPresenterImpl>(),
        MainActivityPresenter.View {
    lateinit var application : KotlinApplication
    val userDao : UserDao = UserDao(this)
    lateinit var user: UserBean
    lateinit var locationService: LocationService
    lateinit var myListener: BDLocationListener
    lateinit var mBaiduMap: BaiduMap
    lateinit var main_BDlocation: BDLocation
    var isFirstLoc: Boolean = true

    override fun refreshView(data: ProjectBean) {
        tv_profile_project.text = data.abbreviation
    }

    override fun initData() {
        mPresenter = MainActivityPresenterImpl()
        application = getApplication() as KotlinApplication
        SDKInitializer.initialize(getApplicationContext())

    }

    override fun loadData() {
        user = userDao.queryUser(application.useridApp)!!
        if (user.projectid != 0) {
            mPresenter.findProject(user.projectid!!)
        } else {
            mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
            mLoadingPage!!.showPage()
        }
    }

    override fun initView() {
        getPermission()
        //左边profile页面
        tv_profile_username.text = user.username
        tv_profile_department.text = user.department
        tv_profile_phone.text = user.phone
        if (user.projectid == 0) {
            tv_profile_project.text = null
        }
        ll_profile_changepwd.setOnClickListener { view ->
            startActivity<ChangePwdActivity>()
        }

    }

    internal fun initMap() {
        val wm1: WindowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int = wm1.getDefaultDisplay().width
        val height: Int = wm1.getDefaultDisplay().height
        mBaiduMap = mv_menuContent_map.map
        mBaiduMap.isMyLocationEnabled = true
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(MapStatus.Builder().zoom(18f).build()))
        mBaiduMap.setOnMapLoadedCallback(BaiduMap.OnMapLoadedCallback {
            mv_menuContent_map.setZoomControlsPosition(Point(width - 150, height - 500))
        })
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService = LocationService(this)
        //创建监听对象
        myListener = MyLocationListener()
        //注册监听
        locationService.registerListener(myListener)
        mBaiduMap.setMyLocationConfiguration(
                MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null))

        var type: Int = getIntent().getIntExtra("from", 0)
        if (type == 0) {
            locationService.setLocationOption(locationService.defaultLocationClientOption)
        } else if (type == 1) {
            locationService.setLocationOption(locationService.option)
        }
        locationService.start()

    }
    /**
     * 安卓6.0后动态获取权限
     */
    fun getPermission(){
        //动态获取权限
        if (AndPermission.hasPermission(this, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            initMap()
        } else {
            // 请求用户授权。
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .send()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // 只需要调用这一句，第一个参数是当前Acitivity/Fragment，回调方法写在当前Activity/Framgent。
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    // 成功回调的方法，用注解即可，里面的数字是请求时的requestCode。
    @PermissionYes(100)
    private fun getYes(grantedPermissions: List<String>) {
        // TODO 申请权限成功。
//        info("成功")
        initMap()
    }

    // 失败回调的方法，用注解即可，里面的数字是请求时的requestCode。
    @PermissionNo(100)
    private fun getNo(deniedPermissions: List<String>) {
        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第一种：用默认的提示语。
            AndPermission.defaultSettingDialog(this, 1).show()
        }
        info("失败")

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
        override fun onConnectHotSpotMessage(p0: String?, p1: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onReceiveLocation(location: BDLocation?) {
            // TODO Auto-generated method stub

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