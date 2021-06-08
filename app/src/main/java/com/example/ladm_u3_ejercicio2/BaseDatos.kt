package com.example.ladm_u3_ejercicio2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class BaseDatos
    (context: Context?,
     name: String?,
     factory: SQLiteDatabase.CursorFactory?,
     version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(bd: SQLiteDatabase) {
        bd.execSQL("CREATE TABLE APARTADO(IDAPARTADO INTEGER PRIMARY KEY AUTOINCREMENT ,NOMBRECLIENTE VARCHAR(200),NOMBRE_PREODUCTO VARCHAR(200),PRECIO FLOAT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }


}