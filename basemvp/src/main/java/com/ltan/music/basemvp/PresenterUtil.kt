package com.ltan.music.basemvp

import com.ltan.music.common.ReflectUtil
import java.lang.reflect.ParameterizedType

/**
 * TMusic.com.ltan.music.basemvp
 *
 * @ClassName: PresenterUtil
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-28
 * @Version: 1.0
 */
object PresenterUtil {
    @JvmStatic
    fun isParameterizedType(cls: Class<*>): Boolean {
        val type = cls.genericSuperclass
        return type != null && ParameterizedType::class.java.isAssignableFrom(type.javaClass)
    }

    @JvmStatic
    fun <T> getBasePresenter(cls: Class<*>): T? {
        if (isParameterizedType(cls)) {
            val types = (cls.genericSuperclass as ParameterizedType).actualTypeArguments
            if (types.isNotEmpty()) {
                // do not check
                val clazz = ReflectUtil.getClass(types[0]) as Class<T>? ?: return null
                return try {
                    if (IBasePresenter::class.java.isAssignableFrom(clazz)) {
                        clazz.newInstance()
                    } else {
                        throw Exception("You must set the Presenter")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        } else {
            if (!IBaseView::class.java.isAssignableFrom(cls)) return null
            return getBasePresenter(cls.superclass)
        }
        return null
    }
}