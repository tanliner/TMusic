package com.ltan.music

import androidx.fragment.app.Fragment

/**
 * TMusic.com.ltan.music
 *
 * to generate fragment instance
 *
 * @ClassName: FragmentFactory
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-27
 * @Version: 1.0
 */
object FragmentFactory  {
    fun <T: Fragment> getInstance(t: Class<T>): Fragment? {
        try {
            return t.newInstance()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }
        return null
    }
}