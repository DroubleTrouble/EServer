package com.ly.eserver.db.dao

import android.content.Context
import com.ly.eserver.bean.UserBean
import com.ly.eserver.db.MyDatabaseOpenHelper
import com.ly.eserver.db.tables.UserTable
import org.jetbrains.anko.db.*
import org.jetbrains.anko.*
/**
 * User表的操作类
 * Created by Max on 2017/7/25.
 */
class UserDao(var mContext: Context) : AnkoLogger{
    var dbOpenHelper : MyDatabaseOpenHelper
    init {
        dbOpenHelper = MyDatabaseOpenHelper(mContext)
    }
    private fun converDomain2Map(data: UserBean): MutableMap<String, Any?> {
        var result = mutableMapOf<String, Any?>()
        //难点2
        with(data){
            result[UserTable.USERID] = userid
            result[UserTable.USERNAME] = username
            result[UserTable.PASSWORD] = password
            result[UserTable.PROJECTID] = projectid
            result[UserTable.PHONE] = phone
            result[UserTable.PIN1] = PIN1
            result[UserTable.PIN2] = PIN1
            result[UserTable.DEPARTMENT] = department
        }
        return result
    }
    /**
     * 保存数据到数据库中
     * */
    fun saveUser(datas: UserBean) {
        //难点1
        dbOpenHelper.use {
            //难点3
            val varargs = converDomain2Map(datas).map { Pair(it.key, it.value) }.toTypedArray()
            replace(UserTable.NAME, *varargs)
        }
    }
    /**
     * @return  UserBean? 返回的数据可能为null
     * */
    fun queryUser(id : Int): UserBean? {
        var user: UserBean? = null
        dbOpenHelper.use {
            val selectQueryBuilder = select(
                    UserTable.NAME,
                    UserTable.USERID,
                    UserTable.USERNAME,
                    UserTable.PASSWORD,
                    UserTable.PROJECTID,
                    UserTable.PHONE,
                    UserTable.PIN1,
                    UserTable.PIN2,
                    UserTable.DEPARTMENT)
            .whereArgs("(userid = {id} )",
                    "id" to id)    //设置查询条件
            //难点4
            user = selectQueryBuilder.parseOpt(object : MapRowParser<UserBean> {
                override fun parseRow(columns: Map<String, Any?>): UserBean {
                    val username = columns[UserTable.USERNAME] as String?
                    val password = columns[UserTable.PASSWORD] as String?  //密码
                    val projectid = columns[UserTable.PROJECTID] as Long? //项目号
                    val phone = columns[UserTable.PHONE] as String?  //手机号
                    val PIN1 = columns[UserTable.PIN1] as String?  //设备号1
                    val PIN2 = columns[UserTable.PIN2] as String? //设备号2
                    val department= columns[UserTable.DEPARTMENT] as String?    //部门
                    val projectid2 :Int = projectid!!.toInt()
                    return UserBean(id, username, password, projectid2, phone, PIN1, PIN2, department)
                }
            })
        }
        return user
    }
}