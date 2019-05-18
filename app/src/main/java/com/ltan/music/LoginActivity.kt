package com.ltan.music

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.ltan.music.account.utils.AccountUtil
import com.ltan.music.basemvp.BaseMVPActivity
import com.ltan.music.common.MusicLog
import com.ltan.music.contract.LoginContract
import com.ltan.music.presenter.LoginPresenter
import kotterknife.bindView
import java.math.BigInteger
import java.security.MessageDigest
import android.text.Editable as TextEditable

/**
 * TMusic.com.ltan.music
 *
 * @ClassName: LoginActivity
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-11
 * @Version: 1.0
 */

class LoginActivity : BaseMVPActivity<LoginPresenter>(), LoginContract.View {

    companion object {
        const val TAG = "Login/Act"
    }

    override fun initLayout(): Int {
        return R.layout.app_activity_login
    }

    override fun initPresenter() {
        mPresenter.attachView(this)
    }

    private val mUsrName: EditText by bindView(R.id.et_app_usr_name)
    private val mUsrPass: EditText by bindView(R.id.et_app_usr_pass)
    private val mLogin: Button by bindView(R.id.btn_app_login)
    private val mLogout: Button by bindView(R.id.btn_app_logout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initField()
        // Kotlin lambda
        mLogin.setOnClickListener {
            val name = mUsrName.text.toString()
            val md5 = MessageDigest.getInstance("MD5")
            val hash = md5.digest(mUsrPass.text.toString().toByteArray())
            val bi = BigInteger(1, hash)
            val passHexString = bi.toString(16).padStart(32, '0')

            MusicLog.d(TAG, "onclick userName: $name $passHexString")

            mPresenter.login(name, passHexString)
        }

        mLogout.setOnClickListener {
            mPresenter.logout()
        }
    }

    private fun initField() {
        // mUsrName.setText("test@163.com")
        // mUsrPass.setText("test_password")

        val account = AccountUtil.getAccountInfo()
        val profile = AccountUtil.getProfileInfo()
        MusicLog.d(TAG, "account is: $account\nprofile is: $profile")

        // Encryptor.randomBytes(16)
        // val key = "0CoJUm6Qyw8W8jud"
        // val estr = Encryptor.encrypt("tanlin", Encryptor.strToByteArray(key))
        // val cstr = Encryptor.decrypt(estr, Encryptor.strToByteArray(key))

        // MusicLog.d(TAG, "init enstr: $estr end")
        // MusicLog.d(TAG, "init clear str: $cstr")
        // mUsrPass.setText("123456789aAd")
    }

    override fun onLoginSuccess() {
    }

    override fun onLogoutSuccess() {
    }
}