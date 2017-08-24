package com.ly.eserver.ui.activity.base

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.http.LifeSubscription
import com.ly.eserver.http.Stateful
import com.ly.eserver.ui.widgets.LoadingPage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.*
import shinetechzz.com.vcleaders.presenter.base.BasePresenter
import com.ly.eserver.app.Constants
import android.os.Handler
import android.os.Message
import com.ly.eserver.service.BluetoothService
import com.ly.eserver.service.DeviceControl
import com.ly.eserver.util.BluetoothSet
import java.lang.ref.WeakReference

/**
 * Created by zengwendi on 2017/6/12.
 */

abstract class BaseActivity<T : BasePresenter<*>> : AppCompatActivity(), LifeSubscription, Stateful ,AnkoLogger{
    val SET_NAME = "blue_set"//设置文件名称
    val mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    lateinit var mPresenter: T
    var mLoadingPage: LoadingPage? = null
    lateinit var mHandler: Handler
    lateinit var mBroadcastReceiver: BroadcastReceiver
    lateinit var intentFilter: IntentFilter
    val REQUEST_CONTACTS : Int = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        去除标题栏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.statusBarColor = Color.TRANSPARENT
//        }
        mHandler = BaseHandler(this)
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                onBroadcastReceive(context, intent)
            }
        }
        intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);//搜索发现设备
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
        registerReceiver(mBroadcastReceiver, intentFilter);
        initData()
        mPresenter!!.attachView(this)
        mLoadingPage = object : LoadingPage(this) {
            override fun initView() {
                this@BaseActivity.initView()
            }

            override fun loadData() {
                this@BaseActivity.loadData()
            }

            override fun getLayoutId(): Int {
                return this@BaseActivity.layoutId
            }
        }
        setContentView(mLoadingPage)
        loadData()
    }

    //用于监听rxjava防止内存泄露
    override fun bindSubscription(disposable: Disposable) {
        mCompositeDisposable!!.add(disposable)
    }


     override fun onResume() {
        super.onResume()
        //注册广播
        this.registerReceiver(mBroadcastReceiver, intentFilter)
    }

    public override fun onPause() {
        super.onPause()
        //取消注册广播
        this.unregisterReceiver(mBroadcastReceiver)
    }

    /**
     * 消息接收器
     * @author Xuqn
     */
    internal class BaseHandler(activity: BaseActivity<*>) : Handler() {
        var mActivity: WeakReference<BaseActivity<*>>
        init {
            mActivity = WeakReference<BaseActivity<*>>(activity)
        }
        override fun handleMessage(msg: Message) {
            mActivity.get()!!.onHandlerReceive(msg)
        }
    }

    /**
     * 消息接收器
     * @param msg
     */
    protected abstract fun onHandlerReceive(msg: Message)

    /**
     * 广播接收器
     * @param context
     * @param intent
     */
    protected abstract fun onBroadcastReceive(context: Context, intent: Intent)


    override fun setState(state: Int) {
        mLoadingPage!!.state = state
        mLoadingPage!!.showPage()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCompositeDisposable != null && mCompositeDisposable.isDisposed) {
            mCompositeDisposable.dispose()
        }
        mPresenter!!.detachView()
        mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 检测设备状态
     * @return
     */
    fun checkDeviceStatus(): Boolean {
        if (this.getSharedPreferences(SET_NAME, Context.MODE_PRIVATE).getInt(BluetoothSet.QUERY_DEVICE, -1) == Constants.QUERY_BLUETOOTH) {
            val bluetooth = BluetoothAdapter.getDefaultAdapter()
            if (bluetooth.isEnabled) {
                //蓝牙开启
                val handler = DeviceControl.instance.bluetoothandler
                if (handler != null) {
                    //蓝牙服务开启
                    val status = handler.service.status
                    //蓝牙连接开启
                    if (status === BluetoothService.BluetoothStatus.START) {
                        return true
                    } else if (status === BluetoothService.BluetoothStatus.PAUSE && handler.service.preStatus === BluetoothService.BluetoothStatus.START) {
                        return true
                    } else if (status === BluetoothService.BluetoothStatus.STARTING) {
                        ToastUtils.showShort("蓝牙设备连接中,请稍后重试")
                    } else {
                        //开始蓝牙搜索
                        handler.service.start(null)
                        ToastUtils.showShort("无法连接默认蓝牙抄读器,请开启蓝牙外设或设置新的蓝牙抄读器")
                    }
                } else {
                    ToastUtils.showShort("蓝牙服务开启中,请稍后重试")
                }
            } else {
                ToastUtils.showShort("未开启蓝牙,请开启蓝牙设备")
            }
        } else {
            //非蓝牙设备不需要检测
            return true
        }
        return false
    }

    /**
     * ①
     * 初始化数据接收intent的数据
     * 设置ActionBar
     */
    abstract fun initData()

    /**
     * ②
     * 请求网络获取的数据返回状态
     * * 如果是静态页面不需要网络请求的在子类的loadData方法中添加以下2行即可
     * mLoadingPage.state = STATE_SUCCESS;
     * mLoadingPage.showPage();
     * 或者调用setState(AppConstants.STATE_SUCCESS)
     */
    abstract fun loadData()

    /**
     * ③
     * 网络请求成功再去加载布局
     * @return
     */
    abstract val layoutId: Int

    /**
     * ④
     * 子类关于View的操作(如setAdapter)都必须在这里面，会因为页面状态不为成功，而binding还没创建就引用而导致空指针。
     * loadData()和initView只执行一次，如果有一些请求需要二次的不要放到loadData()里面。
     */
    abstract fun initView()


}
