package com.ltan.music.widget

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * TMusic.com.ltan.music.widget
 *
 * @ClassName: SongListCategoryItem
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class SongListCategoryItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {

    companion object {
        const val ANIM_DURATION = 300L
        const val EXPAND_DEGREE = 90.0F
        const val SHRINK_DEGREE = 0.0F
    }

    enum class ClickType(val tName: String) {
        MENU("menu"), CREATOR("plus"), ITEM("item");
    }

    enum class State {
        EXPAND, SHRINK
    }

    override fun onClick(v: View) {
        when(v) {
            mMenuImgIv -> listener.onClick(v, ClickType.MENU)
            mCreateImgIv -> listener.onClick(v, ClickType.CREATOR)
            this -> {
                doAnimation()
                listener.onClick(v, ClickType.ITEM)
            }
        }
    }
    private lateinit var listener: ClickListener

    private var mPrevImgIv: ImageView
    private var mItemNameTv: TextView
    private var mCountTv: TextView
    private var mCreateImgIv: ImageView
    private var mMenuImgIv: ImageView

    public fun setState(s: State) {
        mState = s
    }

    public fun getState(): State {
        return mState
    }
    var mState = State.EXPAND

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.song_list_category_item, this, true)
        mPrevImgIv = findViewById(R.id.iv_song_list_category_preview)
        mItemNameTv = findViewById(R.id.tv_song_list_category_name)
        mCountTv = findViewById(R.id.tv_song_list_category_count)
        mMenuImgIv = findViewById(R.id.iv_song_list_category_menu)
        mCreateImgIv = findViewById(R.id.iv_song_list_category_create)
        // val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val pTop = resources.getDimensionPixelSize(R.dimen.song_list_header_name_padding_v)
        val pBottom = resources.getDimensionPixelSize(R.dimen.song_list_header_name_padding_v)
        val pLeft = resources.getDimensionPixelSize(R.dimen.song_list_header_left_padding)
        val pRight = resources.getDimensionPixelSize(R.dimen.song_list_header_right_padding)
        setPadding(pLeft, pTop, pRight, pBottom)

        mPrevImgIv.setImageDrawable(context.getDrawable(R.drawable.right_arrow_little))
        setOnClickListener(this)
        mMenuImgIv.setOnClickListener(this)
        mCreateImgIv.setOnClickListener(this)

        // set the default rotate
        if (mState == State.EXPAND) {
            mPrevImgIv.rotation = EXPAND_DEGREE
        }
    }

    fun setName(name: String) {
        mItemNameTv.text = name
    }

    fun setCount(count: Int) {
        val s = resources.getString(R.string.collector_item_count)
        mCountTv.text = s.format(count)
    }

    fun setClickListener(listener: ClickListener) {
        this.listener = listener
    }

    fun setCreateable(b: Boolean) {
        if (b) {
            mCreateImgIv.visibility = View.VISIBLE
        } else {
            mCreateImgIv.visibility = View.GONE
        }
    }

    private fun doAnimation() {
        if (mState == State.EXPAND) {
            mState = State.SHRINK
            doAnimationShrink()
        } else {
            mState = State.EXPAND
            doAnimationExpand()
        }
    }

    private fun doAnimationExpand() {
        val animExpand = PropertyValuesHolder.ofFloat("rotation", SHRINK_DEGREE, EXPAND_DEGREE)
        /* other animation way, {@link doAnimationShrink} */
        val rotateAnim = ObjectAnimator.ofPropertyValuesHolder(mPrevImgIv, animExpand)
        rotateAnim.duration = ANIM_DURATION
        rotateAnim.start()
    }

    private fun doAnimationShrink() {
        val animShrink = PropertyValuesHolder.ofFloat("rotation", EXPAND_DEGREE, SHRINK_DEGREE)
        val rotateAnim = ObjectAnimator()
        rotateAnim.target = mPrevImgIv
        rotateAnim.duration = ANIM_DURATION
        rotateAnim.setValues(animShrink)
        rotateAnim.start()
    }

    interface ClickListener {
        /**
         * [v] which view has been clicked
         * [type] as the [v], a useful Integer value
         */
        fun onClick(v: View, type: ClickType)
    }
}