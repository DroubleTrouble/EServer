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
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 创建数据库
        db.createTable(UserTable.NAME, true,
                UserTable.USERNO to INTEGER + PRIMARY_KEY,
                UserTable.USERID to INTEGER,
                UserTable.USERNAME to TEXT,
                UserTable.PASSWORD to TEXT,
                UserTable.PHONE to TEXT,
                UserTable.PROJECTID to INTEGER,
                UserTable.PIN1 to TEXT,
                UserTable.PIN2 to TEXT,
                UserTable.DEPARTMENT to TEXT
        )
        with(OperlogTable) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `" + NAME + "`(" +
                    OperlogTable.OPERLOGID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    OperlogTable.USERID + " INTEGER, " +
                    OperlogTable.PROJECTID + " INTEGER, " +
                    OperlogTable.TABLEADDRESS + " TEXT," +
                    OperlogTable.LOCATION + " TEXT," +
                    OperlogTable.ADDRESS + " TEXT," +
                    OperlogTable.ISFINISH + " TEXT," +
                    OperlogTable.ISSENDED + " TEXT," +
                    OperlogTable.TYPE + " TEXT," +
                    OperlogTable.TIME + " TEXT,"
                    +OperlogTable.RESULT + " TEXT);" )
        }


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 如果需要的话，在这里升级数据库
        db.dropTable(UserTable.NAME, true)
        db.dropTable(OperlogTable.NAME, true)
        onCreate(db)
    }
}
