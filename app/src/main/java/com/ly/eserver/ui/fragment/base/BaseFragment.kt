package com.ly.eserver.ui.fragment.base

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ly.eserver.http.LifeSubscription
import com.ly.eserver.http.Stateful
import com.ly.eserver.ui.widgets.LoadingPage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.*
import shinetechzz.com.vcleaders.presenter.base.BasePresenter

/**
 * Created by Max on 2017/8/17.
 */
abstract class BaseFragment<T : BasePresenter<*>> : Fragment(), LifeSubscription, Stateful, AnkoLogger {
    val mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    lateinit var mPresenter: T
    var mLoadingPage: LoadingPage? = null

    protected lateinit var mActivity: Activity

    /**
     * 获得全局的，防止使用getActivity()为空
     * @param context
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mActivity = context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mPresenter.attachView(this)
        val view = LayoutInflater.from(mActivity)
                .inflate(layoutId, container, false)
        mLoadingPage = object : LoadingPage(context) {
            override fun initView() {
                this@BaseFragment.initView()
            }

            override fun loadData() {
                this@BaseFragment.loadData()
            }

            override fun getLayoutId(): Int {
                return this@BaseFragment.layoutId
            }
        }
        return view

    }


    //用于监听rxjava防止内存泄露
    override fun bindSubscription(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }
    /**
     * ①
     * 初始化数据接收intent的数据
     * 设置ActionBar
     */
    abstract fun initData()

    override fun setState(state: Int) {
        mLoadingPage!!.state = state
        mLoadingPage!!.showPage()
    }
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