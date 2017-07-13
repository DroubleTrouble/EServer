package com.ly.eserver.http.utils

import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.ly.eserver.bean.DataBean
import com.ly.eserver.http.LifeSubscription
import com.ly.eserver.http.Stateful
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import retrofit2.HttpException
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Created by zengwendi on 2017/6/12.
 *  为网络请求提供一个主构造方法进行结果回调处理
 */
class Callback<T>(val mListener: HttpOnNextListener<Any?>) : Observer<T> ,AnkoLogger{
    var mLifeSubscription: LifeSubscription? = null
    var mTarget: Stateful? = null

    fun detachView() {
        if (mTarget != null) {
            mTarget = null
        }
    }

    fun setLifeSubscription(lifecycle: LifeSubscription) {
        mLifeSubscription = lifecycle
    }

    override fun onNext(value: T) {
        info("Callback onNext")
        if (value is DataBean<*>) {
            onResponse(value)
        }
    }


    //        添加Disposable防止内存泄露
    override fun onSubscribe(d: Disposable?) {
        mLifeSubscription!!.bindSubscription(d!!)
    }

    override fun onError(e: Throwable?) {
    }

    override fun onComplete() {
    }

    /**
     * 统一处理成功回掉
     */
    fun onResponse(data: DataBean<*>) {
        if (data == null) {
            onfail(Throwable())
            mListener.onError()
            return
        }
        if (!data!!.status.equals("200")) {
            ToastUtils.showShort(data.mesg)
            mListener.onError()
            return
        }
        if (mTarget != null)
            mTarget!!.setState(com.ly.eserver.app.Constants.Companion.STATE_SUCCESS)
        error("onResponse")
        mListener!!.onNext(data.data!!)
    }

    fun onfail(e: Throwable) {
        mListener.onError()
        if (!NetworkUtils.isAvailableByPing()) {
            ToastUtils.showShort("你连接的网络有问题，请检查网络连接状态")
            if (mTarget != null) {
                mTarget!!.setState(com.ly.eserver.app.Constants.Companion.STATE_ERROR)
            }
            return
        }
        if (e is HttpException) {
            mTarget!!.setState(com.ly.eserver.app.Constants.Companion.STATE_ERROR)
            ToastUtils.showShort("服务器异常")
            return
        }
        mTarget!!.setState(com.ly.eserver.app.Constants.Companion.STATE_ERROR)
        ToastUtils.showShort("数据异常")
    }
}