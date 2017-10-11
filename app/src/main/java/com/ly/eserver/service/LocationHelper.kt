package com.ly.eserver.service

import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.ly.eserver.app.KotlinApplication
import org.jetbrains.anko.*
/**
 * @ClassName: LocationHelper
 * @author: hewei
 * @version: v1.0
 */
class LocationHelper  constructor() : AnkoLogger{
    var callBack: LocationCallBack? = null
    private var locationClient: LocationClient? = null
    private val locationListener = MyBDLocationListener()

    init {
        //第一步实例化定位核心类
        locationClient = LocationClient(KotlinApplication.instance().applicationContext)
        locationClient!!.locOption = locOption
        //第二步设置位置变化回调监听
        locationClient!!.registerLocationListener(locationListener)
    }

    fun start() {
        //   第三步开始定位
        locationClient!!.start()
        locationClient!!.requestLocation();
    }

    //一般会在Activity的OnDestroy方法调用
    fun stop() {
        if (locationClient != null) {
            locationClient!!.unRegisterLocationListener(locationListener)
            locationClient!!.stop()
            locationClient = null
        }
    }

     //设置定位坐标系
            //重新定位时间间隔
            //option.setScanSpan(60*1000);
            //设置是否打开gps
            //设置定位模式
            //是否需要poi结果
            //        option.setPoiDistance(1000);
            //        option.setPoiExtraInfo(true);
    private val locOption: LocationClientOption
        get() {
            val option = LocationClientOption()
            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
            //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            option.setCoorType("bd09ll")
            //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            option.setScanSpan(1000)
            //可选，设置是否需要地址信息，默认不需要
            option.setIsNeedAddress(true)
            //可选，设置是否需要地址描述
            option.setIsNeedLocationDescribe(true)
            //可选，设置是否需要设备方向结果
            option.setNeedDeviceDirect(false)
            //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结
            option.isLocationNotify = false
            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            option.setIgnoreKillProcess(true)
            //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            option.setIsNeedLocationDescribe(true)
            //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            option.setIsNeedLocationPoiList(true)
            //可选，默认false，设置是否收集CRASH信息，默认收集
            option.SetIgnoreCacheException(false)
            return option
        }

    private inner class MyBDLocationListener : BDLocationListener {
        override fun onReceiveLocation(bdLocation: BDLocation?) {
            if (callBack != null && bdLocation != null) {
                callBack!!.callBack(bdLocation)
                info("onReceiveLocation")
            }
            //多次定位必须要调用stop方法
            locationClient!!.stop()
        }

    }

    interface LocationCallBack {
        fun callBack(bdLocation: BDLocation)
    }

    companion object {
        private var helper: LocationHelper? = null

        val instance: LocationHelper
            get() {
                if (helper == null) {
                    helper = LocationHelper()
                }
                return helper!!
            }
    }
}

