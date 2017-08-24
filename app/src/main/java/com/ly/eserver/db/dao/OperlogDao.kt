package com.ly.eserver.db.dao

import android.content.Context
import com.ly.eserver.bean.OperlogBean
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.MyDatabaseOpenHelper
import com.ly.eserver.db.tables.OperlogTable
import org.jetbrains.anko.db.*
import retrofit2.http.OPTIONS
import java.util.*

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
            results[OperlogTable.ISSENDED] = issended
            results[OperlogTable.TIME] = time.toString()
            results[OperlogTable.RESULT] = result

        }
        return results
    }
    fun queryOperlog(): List<OperlogBean>? {
        var operlogBeans: List<OperlogBean>? = null
        dbOpenHelper.use {
            val selectQueryBuilder = select(
                    OperlogTable.NAME,
                    OperlogTable.OPERLOGID,
                    OperlogTable.USERID,
                    OperlogTable.ADDRESS,
                    OperlogTable.LOCATION,
                    OperlogTable.TABLEADDRESS,
                    OperlogTable.TYPE,
                    OperlogTable.ISFINISH,
                    OperlogTable.ISSENDED,
                    OperlogTable.TIME,
                    OperlogTable.RESULT)
                    .whereArgs("(issended = {issended} )",
                            "issended" to false)    //设置查询条件
            //难点4
            operlogBeans = selectQueryBuilder.parseList(object : MapRowParser<OperlogBean> {
                override fun parseRow(columns: Map<String, Any?>): OperlogBean {
                    val operlogid = columns[OperlogTable.OPERLOGID] as Long?
                    val id = operlogid!!.toInt()
                    val userid = columns[OperlogTable.USERID] as Long?  //userid
                    val user = userid!!.toInt()
                    val ADDRESS = columns[OperlogTable.ADDRESS] as String?
                    val location = columns[OperlogTable.LOCATION] as String?
                    val tableaddress = columns[OperlogTable.TABLEADDRESS] as String?
                    val type = columns[OperlogTable.TYPE] as String?
                    val isfinish = columns[OperlogTable.ISFINISH] as String
                    val issended = columns[OperlogTable.ISSENDED] as String
                    val isfin = isfinish.toBoolean()
                    val issend = issended.toBoolean()
                    val time = columns[OperlogTable.TIME] as String?
                    val date = Date(time)
                    val result = columns[OperlogTable.RESULT] as String?
                    return OperlogBean(id, user, tableaddress, location, ADDRESS, type, isfin,result,issend, date)
                }
            })
        }
        return operlogBeans
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