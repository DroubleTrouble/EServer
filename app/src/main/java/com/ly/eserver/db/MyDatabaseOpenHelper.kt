package com.ly.eserver.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.ly.eserver.db.tables.OperlogTable
import com.ly.eserver.db.tables.UserTable
import org.jetbrains.anko.db.*

/**
 * Created by Max on 2017/7/25.
 */
class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 创建数据库
        db?.createTable(UserTable.NAME, true,
                UserTable.USERID to INTEGER + PRIMARY_KEY,
                UserTable.USERNAME to TEXT,
                UserTable.PASSWORD to TEXT,
                UserTable.PHONE to TEXT,
                UserTable.PROJECTID to INTEGER,
                UserTable.PIN1 to TEXT,
                UserTable.PIN2 to TEXT,
                UserTable.DEPARTMENT to TEXT
        )
        db?.createTable(OperlogTable.NAME, true,
                OperlogTable.OPERLOGID to INTEGER + PRIMARY_KEY,
                OperlogTable.USERID to INTEGER,
                OperlogTable.TABLEADDRESS to TEXT,
                OperlogTable.LOCATION to TEXT,
                OperlogTable.ADDRESS to TEXT,
                OperlogTable.ISFINISH to TEXT,
                OperlogTable.TYPE to TEXT,
                OperlogTable.TIME to TEXT,
                OperlogTable.RESULT to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 如果需要的话，在这里升级数据库
        db?.dropTable(UserTable.NAME, true)
        db?.dropTable(OperlogTable.NAME, true)
        onCreate(db)
    }
}
