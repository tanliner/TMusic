package com.ltan.music

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.ltan.music.basemvp.BaseMVPActivity
import com.ltan.music.common.MusicLog
import com.ltan.music.presenter.LoginPresenter
import kotterknife.bindView
import java.math.BigInteger
import java.nio.charset.Charset
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

class LoginActivity : BaseMVPActivity<LoginPresenter>() {

    companion object {
        const val TAG = "Login/Act"
    }

    override fun initLayout(): Int {
        return R.layout.app_activity_login
    }

    private val mUsrName: EditText by bindView(R.id.et_app_usr_name)
    private val mUsrPass: EditText by bindView(R.id.et_app_usr_pass)
    private val mLogin: Button by bindView(R.id.btn_app_login)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kotlin lambda
        mLogin.setOnClickListener {
            val name = mUsrName.text.toString()
            val md5 = MessageDigest.getInstance("MD5")
            val hash = md5.digest(mUsrPass.text.toString().toByteArray(Charset.forName("utf-8")))
            val bi = BigInteger(1, hash)
            val passHexString = bi.toString(16).padStart(32, '0')

            // c4ca4238a0b923820dcc509a6f75849b
            // c4ca4238a0b923820dcc509a6f75849b
            MusicLog.d(TAG, "onclick userName: $name $passHexString")

            mPresenter.login(name, passHexString)
        }
        mUsrName.setText("tl_friend@163.com")
        // mUsrPass.setText("TanLin9211")
        mUsrPass.setText("123456789aAd")
    }
}