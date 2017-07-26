package com.ly.eserver.ui.util

import android.content.Context

import android.content.SharedPreferences


/**
 * Created by xialo on 2016/7/25.
 */

object SharedPreferencesUtil {

    private val spUser = "user"
    private val spFileName = "welcomePage"
    val FIRST_OPEN = "first_open"

    fun getBoolean(context: Context, strKey: String,
                   strDefault: Boolean?): Boolean? {//strDefault	boolean: Value to return if this preference does not exist.
        val setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE)
        val result = setPreferences.getBoolean(strKey, strDefault!!)
        return result
    }

    fun putBoolean(context: Context, strKey: String,
                   strData: Boolean?) {
        val activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE)
        val editor = activityPreferences.edit()
        editor.putBoolean(strKey, strData!!)
        editor.commit()
    }

    fun putUser(context: Context, userid : Int,
                password : String){
        val activityPreferences = context.getSharedPreferences(
                spUser, Context.MODE_PRIVATE)
        val editor = activityPreferences.edit()
        editor.putInt("userid", userid)
        editor.putString("password",password)
        editor.commit()
    }

    fun getString(context: Context, strKey: String) : String? {
        val setPreferences = context.getSharedPreferences(
                spUser, Context.MODE_PRIVATE)
            val result = setPreferences.getString(strKey, null)
            return result
    }
    fun getInt(context: Context, strKey: String) : Int? {
        val setPreferences = context.getSharedPreferences(
                spUser, Context.MODE_PRIVATE)
        val result = setPreferences.getInt(strKey, 0)
        return result
    }
}