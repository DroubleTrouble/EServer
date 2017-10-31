package com.ly.eserver.bean

import java.io.Serializable

/**
 * Created by Max on 2017/10/25.
 */
data class VersionBean constructor(
        val name: String? = null,
        val note: String? = null,
        var url : String? = null,
        val code: Int = 0
): Serializable
