package com.ltan.music.common

import android.animation.Animator

/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: SimpleAnimator
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-02
 * @Version: 1.0
 */
open class SimpleAnimator : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {}

    override fun onAnimationCancel(animation: Animator?) {}

    override fun onAnimationStart(animation: Animator?) {}
}