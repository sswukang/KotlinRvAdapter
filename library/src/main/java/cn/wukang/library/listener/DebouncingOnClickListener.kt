package cn.wukang.library.listener

import android.view.View

/**
 * 单击防抖动
 *
 * @author wukang
 */
abstract class DebouncingOnClickListener : View.OnClickListener {
    private companion object {
        private var enabled = true

        private val ENABLE_AGAIN = { enabled = true }
    }

    override fun onClick(v: View) {
        if (enabled) {
            enabled = false
            v.post(ENABLE_AGAIN)
            doClick(v)
        }
    }

    abstract fun doClick(v: View)
}