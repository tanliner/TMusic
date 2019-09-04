package com.ltan.music.service.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * TMusic.com.ltan.music.service.provider
 *
 * @ClassName: MusicDbHelper
 * @Description:
 * @Author: tanlin
 * @Date:   2019-09-03
 * @Version: 1.0
 */
class MusicDbHelper(ctx: Context?, name: String, fac: SQLiteDatabase.CursorFactory?, ver: Int) :
    SQLiteOpenHelper(ctx, name, fac, ver) {
    companion object {
        const val USER_TB_NAME = "users"
        const val SONG_TB_NAME = "song_unavailable"
    }
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE IF NOT EXISTS $USER_TB_NAME (_id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT)")
        db?.execSQL("CREATE TABLE IF NOT EXISTS $SONG_TB_NAME (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}