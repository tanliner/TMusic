package com.ltan.music.account.utils

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.ltan.music.account.beans.Account
import com.ltan.music.account.beans.Profile
import com.ltan.music.common.BaseApplication
import com.ltan.music.common.Constants

/**
 * TMusic.com.ltan.music.account
 * data-class
 *
 * @ClassName: Profile
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-17
 * @Version: 1.0
 */
object AccountUtil {
    @JvmStatic
    fun saveAccountInfo(account: Account?) {
        val gson = Gson()
        val spf = BaseApplication.getAPPContext()
            .getSharedPreferences(Constants.LOCAL_SP_ACCOUNT_NAME, Context.MODE_PRIVATE)
        val gsonStr = gson.toJson(account)
        spf.edit()
            .putString(Constants.LOCAL_SP_ACCOUNT_SECTION, gsonStr)
            .commit()
    }

    @JvmStatic
    fun saveProfileInfo(profile: Profile?) {
        val gson = Gson()
        val spf = BaseApplication.getAPPContext()
            .getSharedPreferences(Constants.LOCAL_SP_ACCOUNT_NAME, Context.MODE_PRIVATE)
        val gsonStr = gson.toJson(profile)
        spf.edit()
            .putString(Constants.LOCAL_SP_PROFILE_SECTION, gsonStr)
            .commit()
    }

    @JvmStatic
    fun getAccountInfo(): Account? {
        val gson = Gson()
        val spf = BaseApplication.getAPPContext()
            .getSharedPreferences(Constants.LOCAL_SP_ACCOUNT_NAME, Context.MODE_PRIVATE)
        val str = spf.getString(Constants.LOCAL_SP_ACCOUNT_SECTION, "")

        if(TextUtils.isEmpty(str)) {
            return null
        }
        return gson.fromJson(str, Account::class.java)
    }

    @JvmStatic
    fun getProfileInfo(): Profile? {
        val gson = Gson()
        val spf = BaseApplication.getAPPContext()
            .getSharedPreferences(Constants.LOCAL_SP_ACCOUNT_NAME, Context.MODE_PRIVATE)
        val str = spf.getString(Constants.LOCAL_SP_PROFILE_SECTION, "")

        if(TextUtils.isEmpty(str)) {
            return null
        }
        return gson.fromJson(str, Profile::class.java)
    }
}