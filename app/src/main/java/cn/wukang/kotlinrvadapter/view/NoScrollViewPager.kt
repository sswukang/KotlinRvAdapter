package cn.wukang.kotlinrvadapter.view

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * 不可手势滑动的ViewPager
 *
 * @author wukang
 */
class NoScrollViewPager(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean = false

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = false
}