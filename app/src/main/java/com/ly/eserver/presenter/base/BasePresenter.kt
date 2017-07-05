package shinetechzz.com.vcleaders.presenter.base

import com.ly.eserver.http.LifeSubscription
import com.ly.eserver.http.utils.Callback
import com.ly.eserver.http.utils.HttpUtils
import com.ly.eserver.presenter.base.BaseView
import io.reactivex.Observable

/**
 * Created by zengwendi on 2017/6/12.
 */

open class BasePresenter<T : BaseView<*>> {
    protected var mView: T? = null//指的是界面，也就是BaseFragment或者BaseActivity

    private var callback: Callback<*>? = null

    fun attachView(mLifeSubscription: LifeSubscription) {
        this.mView = mLifeSubscription as T
    }

    fun <T> invoke(observable: Observable<T>, callback: Callback<T>) {
        this.callback = callback
        HttpUtils.invoke(mView as LifeSubscription, observable, callback)
    }

    fun detachView() {
        if (mView != null)
            mView = null
        if (callback != null) {
            callback!!.detachView()
        }

    }
}
