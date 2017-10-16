package com.ly.eserver.ui.activity.main

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import android.widget.Toast
import com.amap.api.maps.model.MyLocationStyle
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
import com.ly.eserver.ui.activity.BlueToothActivity
import com.ly.eserver.ui.activity.ChangePwdActivity
import com.ly.eserver.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.menu_content_home.*
import kotlinx.android.synthetic.main.menu_left_profile.*
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import java.util.*
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.*
import com.amap.api.location.AMapLocation
import com.ly.eserver.ui.activity.MenuActivity
import com.ly.eserver.ui.activity.ReimbursementActivity


/**
 * 主页面
 * Created by zengwendi on 2017/6/12.
 */
class MainActivity(override val layoutId: Int = R.layout.activity_main) : BaseActivity<MainActivityPresenterImpl>(),
        MainActivityPresenter.View,LocationSource, AMapLocationListener {

    val myLocationStyle :MyLocationStyle = MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION
    lateinit var aMap : AMap
    lateinit var mUiSettings: UiSettings    //定义一个UiSettings对象
    var amapLocation : AMapLocation? = null
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private var isFirstLoc = true

    //定位需要的数据
    var mListener: LocationSource.OnLocationChangedListener? = null
    var mlocationClient: AMapLocationClient? = null
    var mLocationOption: AMapLocationClientOption? = null

    val userDao: UserDao = UserDao(this)
    lateinit var user: UserBean
    var check: CheckBean = CheckBean()

    override fun initData() {
        mPresenter = MainActivityPresenterImpl()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mv_menuContent_map.onCreate(savedInstanceState)
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
//                check.address = main_BDlocation!!.locationDescribe
//                check.location = main_BDlocation!!.latitude.toString() + "/" + main_BDlocation!!.longitude.toString()
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
            if (amapLocation != null) {
                startActivity<ReimbursementActivity>("location" to amapLocation!!.description)
            }else{
                startActivity<ReimbursementActivity>("location" to "")
            }
        }

        //地图页面
        setMap()

        val bluetooth = BluetoothAdapter.getDefaultAdapter()
        if (bluetooth.isEnabled) {
            iv_menuContent_bluetooth.setImageResource(R.drawable.close_bluet)
        }
        ll_menucontent_startdata.setOnClickListener {

            if (!bluetooth.isEnabled) {
                ToastUtils.showShort("蓝牙未开启,请打开蓝牙设备!")
                startActivity<BlueToothActivity>()
            } else {
                if (amapLocation != null) {
                    startActivity<MenuActivity>("location" to amapLocation!!)
                } else {
                    startActivity<MenuActivity>("location" to "")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mv_menuContent_map.onResume()
        isFirstLoc = true
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
        mv_menuContent_map.onDestroy();
        super.onDestroy()
        if(mlocationClient!=null)
            mlocationClient!!.onDestroy()
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
            val bluetooth = BluetoothAdapter.getDefaultAdapter()
            bluetooth.disable()
            finish()
            System.exit(0)
        }
    }
    //地图设置
    fun setMap() {
        aMap = mv_menuContent_map.map
        //设置地图的放缩级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16F))
        // 设置定位监听
        aMap.setLocationSource(this)
        mUiSettings = aMap.uiSettings
        mUiSettings.setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        mUiSettings.zoomPosition = AMapOptions.ZOOM_POSITION_RIGHT_CENTER
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationEnabled(true);//显示定位层并可触发定位  

    }

    override fun deactivate() {
        mListener = null;
        if(mlocationClient != null) {
            mlocationClient!!.stopLocation()
            mlocationClient!!.onDestroy()
        }
        mlocationClient = null
    }

    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener?) {
        mListener = onLocationChangedListener
        mlocationClient = AMapLocationClient(this);
        mlocationClient!!.setLocationListener(this);//定位回调监听  
        mLocationOption = AMapLocationClientOption();
        mLocationOption!!.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高进度定位
        mLocationOption!!.interval = 10000
        mlocationClient!!.setLocationOption(mLocationOption);//加载定位参数  
        mlocationClient!!.startLocation();//开始定位  

    }

    //定位回调  在回调方法中调用“mListener.onLocationChanged(amapLocation);”可以在地图上显示系统小蓝点。
    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.errorCode == 0 ) {
                    //点击定位按钮 能够将地图的中心移动到定位点
                if (isFirstLoc){
                    mListener!!.onLocationChanged(aMapLocation)
                    amapLocation = aMapLocation
                    info(amapLocation!!.description)
                    ToastUtils.showShort(amapLocation!!.description.toString())
                    isFirstLoc = false
                }
            } else {
                val errText = "定位失败," + aMapLocation.errorCode + ": " + aMapLocation.errorInfo
                info("定位AmapErr: " + errText)
            }
        }
    }

}