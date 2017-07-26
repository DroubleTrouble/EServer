package com.ly.eserver.db.dao

import android.content.Context
import com.ly.eserver.bean.OperlogBean
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.MyDatabaseOpenHelper
import com.ly.eserver.db.tables.OperlogTable
import org.jetbrains.anko.db.*
import retrofit2.http.OPTIONS

/**
 * Operlog表的操作类
 * Created by Max on 2017/7/25.
 */
class OperlogDao(var mContext: Context){
    var dbOpenHelper : MyDatabaseOpenHelper
    init {
        dbOpenHelper = MyDatabaseOpenHelper(mContext)
    }
    private fun converDomain2Map(data: OperlogBean): MutableMap<String, Any?> {
        var results = mutableMapOf<String, Any?>()
        //难点2
        with(data){
            results[OperlogTable.OPERLOGID] = operlogid
            results[OperlogTable.USERID] = userid
            results[OperlogTable.ADDRESS] = address
            results[OperlogTable.LOCATION] = location
            results[OperlogTable.TABLEADDRESS] = tableAddress
            results[OperlogTable.TYPE] = type
            results[OperlogTable.ISFINISH] = isfinish
            results[OperlogTable.TIME] = time
            results[OperlogTable.RESULT] = result

        }
        return results
    }
    /**
     * 保存数据到数据库中
     * */
    fun saveOperlog(datas: OperlogBean) {
        //难点1
        dbOpenHelper.use {
            //难点3
            val varargs = converDomain2Map(datas).map { Pair(it.key, it.value) }.toTypedArray()
            replace(OperlogTable.NAME, *varargs)
        }
    }

}