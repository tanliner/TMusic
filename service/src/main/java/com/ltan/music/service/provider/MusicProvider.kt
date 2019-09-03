package com.ltan.music.service.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.provider.BaseColumns


/**
 * TMusic.com.ltan.music.service.provider
 *
 * @ClassName: MusicProvider
 * @Description:
 * @Author: tanlin
 * @Date:   2019-09-03
 * @Version: 1.0
 */
class MusicProvider : ContentProvider() {

    companion object {
        const val DB_NAME = "musics"
        // must same as AndroidManifest.xml, ${applicationId}.provider
        const val AUTHORITIES = "com.ltan.music.provider"
        @JvmStatic
        val BASE_URI = Uri.parse("content://$AUTHORITIES")
        const val SONG_UNAVAILABLE = 1
        const val SONG_UNAVAILABLE_PATH = "song_unavailable"

        const val USERS = 2
        const val USERS_PATH = "users"
    }

    private lateinit var mSqliteHelper: SQLiteOpenHelper
    private lateinit var mDatabase: SQLiteDatabase
    private var mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate(): Boolean {
        mSqliteHelper = MusicDbHelper(context, DB_NAME, null, 1)
        mDatabase = mSqliteHelper.writableDatabase
        mUriMatcher.addURI(AUTHORITIES, SONG_UNAVAILABLE_PATH, SONG_UNAVAILABLE)
        mUriMatcher.addURI(AUTHORITIES, USERS_PATH, USERS)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (mUriMatcher.match(uri)) {
            SONG_UNAVAILABLE -> {
                val rowId = mDatabase.insert(MusicDbHelper.SONG_TB_NAME, null, values)
                return ContentUris.withAppendedId(uri, rowId)
            }
            USERS -> {
                val rowId =  mDatabase.insert(MusicDbHelper.USER_TB_NAME, null, values)
                return ContentUris.withAppendedId(uri, rowId)
            }
            // 2, 3 -> {}
            else -> {}
        }
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        when (mUriMatcher.match(uri)) {
            SONG_UNAVAILABLE -> {
                return mDatabase.query(MusicDbHelper.SONG_TB_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            // 2, 3 -> {}
            USERS -> {
                return mDatabase.query(MusicDbHelper.USER_TB_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            else -> {}
        }
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when (mUriMatcher.match(uri)) {
            SONG_UNAVAILABLE -> {
                return mDatabase.update(MusicDbHelper.SONG_TB_NAME, values, selection, selectionArgs)
            }
            USERS -> {
                return mDatabase.update(MusicDbHelper.USER_TB_NAME, values, selection, selectionArgs)
            }
            else -> {}
        }
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (mUriMatcher.match(uri)) {
            SONG_UNAVAILABLE -> {
                return mDatabase.delete(MusicDbHelper.SONG_TB_NAME, selection, selectionArgs)
            }
            USERS -> {
                return mDatabase.delete(MusicDbHelper.USER_TB_NAME, selection, selectionArgs)
            }
            else -> {}
        }
        return 0
    }

    override fun getType(uri: Uri): String? {
        when (mUriMatcher.match(uri)) {
            SONG_UNAVAILABLE -> return SONG_UNAVAILABLE_PATH
            USERS -> return USERS_PATH
            else -> {}
        }
        return null
    }

    class Columns : BaseColumns {
        companion object {
            val NAME: String = "name"
        }
    }
}