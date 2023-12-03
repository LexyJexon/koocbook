package com.example.koocbook

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "koocbook", factory, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE users (id INT PRIMARY KEY, email TEXT, pass TEXT);"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS uses")
        onCreate(db)
    }

    fun addUser(user : User){
        val values = ContentValues()
        values.put("email", user.email)
        values.put("pass", user.password)

        val db = this.writableDatabase
        db.insert("users", null, values)

        db.close()
    }

    fun getUser(login: String, password: String): Boolean {
        val db = this.readableDatabase

        val res = db.rawQuery("SELECT * FROM users WHERE email = '$login' AND pass = '$password';", null)

        return res.moveToFirst()
    }
}