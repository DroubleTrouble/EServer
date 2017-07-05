package com.ly.eserver.bean

import java.io.Serializable

/**
 * Created by Max on 2017/7/5.
 */
data class DataBean<T> constructor(val status: Boolean = false, val results: T? = null) : Serializable {

}