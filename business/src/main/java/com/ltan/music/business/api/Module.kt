package com.ltan.music.business.api

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: Module
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
class Module<T>(var popAdjust: Boolean, val data: T, val code: Int) {
    override fun toString(): String {
        return "Module-${this.hashCode()}:{\npopAdjust: $popAdjust\ndata: $data\ncode: $code}"
    }
}
