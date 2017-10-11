package com.ly.eserver.bean

import java.io.Serializable

/**
 * Created by Max on 2017/7/5.
 */
data class DataBean<T> constructor(val status: String? = null, val mesg : String? = null, val data: T? = null) : Serializable