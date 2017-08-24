package com.ly.eserver.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.Gravity

import com.ly.eserver.R

import java.util.Timer
import java.util.TimerTask

/**
 * @Description:发送验证码的button，带有倒计时，以及在发送的过程中不可点击；
 * * 调用方式 view.startTickWork()方法即可；
 * *
 * @author http://blog.csdn.net/finddreams
 */
class SendValidateButton(context: Context, attrs: AttributeSet) : android.support.v7.widget.AppCompatButton(context, attrs) {
    private var mTimer: Timer? = null
    private var mTask: TimerTask? = null
    private var mDisableTime = DISABLE_TIME // 倒计时时间，默认60秒
    private var mEnableColor = R.color.colorSend
    private var mDisableColor = R.color.colorlightGray
    private var mEnableString: String? = "获取验证码"
    private var mDisableString = "剩余"
    private val Second = "秒"
    private var mClickBle = true
    private var mListener: SendValidateButtonListener? = null

    fun getmDisableTime(): Int {

        return mDisableTime

    }

    fun getmEnableColor(): Int {

        return mEnableColor

    }

    fun setmEnableColor(mEnableColor: Int) {

        this.mEnableColor = mEnableColor

        this.setTextColor(mEnableColor)

    }

    fun getmDisableColor(): Int {
        return mDisableColor
    }

    fun setmDisableColor(mDisableColor: Int) {

        this.mDisableColor = mDisableColor

    }

    fun getmEnableString(): String? {

        return mEnableString

    }

    val isDisable: Boolean
        get() {
            if (mDisableTime > 0) {
                return true
            }
            return false
        }

    fun setmEnableString(mEnableString: String) {

        this.mEnableString = mEnableString

        if (this.mEnableString != null) {

            this.text = mEnableString
        }
    }

    fun getmDisableString(): String {

        return mDisableString

    }

    fun setmDisableString(mDisableString: String) {

        this.mDisableString = mDisableString

    }

    fun setmListener(mListener: SendValidateButtonListener) {

        this.mListener = mListener

    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {

            when (msg.what) {

                MSG_TICK ->

                    tickWork()

                else -> {
                }
            }
            super.handleMessage(msg)
        }
    }

    init {
        initView()
    }

    private fun initView() {
        this.text = mEnableString
        this.gravity = Gravity.CENTER
        this.setTextColor(resources.getColor(mEnableColor))
        initTimer()
        this.setOnClickListener {
            if (mListener != null && mClickBle) {
                // startTickWork();
                mListener!!.onClickSendValidateButton()
            }
        }
    }

    private fun initTimer() {
        mTimer = Timer()
    }

    private fun initTimerTask() {
        mTask = object : TimerTask() {
            override fun run() {
                mHandler.sendEmptyMessage(MSG_TICK)
            }
        }
    }

    fun startTickWork() {
        if (mClickBle) {
            mClickBle = false

            this@SendValidateButton.text = mDisableString + mDisableTime + Second
            this.isEnabled = false
            this@SendValidateButton.setTextColor(resources.getColor(mDisableColor))
            initTimerTask()
            mTimer!!.schedule(mTask, 0, 1000)

        }

    }

    /**
     * 每秒钟调用一次
     */
    private fun tickWork() {

        mDisableTime--

        this.text = mDisableString + mDisableTime + Second

        if (mListener != null) {
            mListener!!.onTick()
        }
        if (mDisableTime <= 0) {
            stopTickWork()
        }
    }

    fun stopTickWork() {
        mTask!!.cancel()
        mTask = null
        mDisableTime = DISABLE_TIME
        this.text = mEnableString

        this.setTextColor(resources.getColor(mEnableColor))
        this.isEnabled = true
        mClickBle = true
    }

    interface SendValidateButtonListener {
        fun onClickSendValidateButton()
        fun onTick()
    }

    companion object {
        private val DISABLE_TIME = 60
        private val MSG_TICK = 0
    }
}
