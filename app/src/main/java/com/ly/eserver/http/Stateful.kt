package com.ly.eserver.http

/**
 * Created by zengwendi on 2017/6/12.
 * 控制显示状态
 */
open interface Stateful {
    fun setState(state: Int)
}