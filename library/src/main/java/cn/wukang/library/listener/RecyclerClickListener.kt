package cn.wukang.library.listener

import android.support.annotation.LayoutRes
import android.view.View

/**
 * KotlinRvAdapter
 *
 * @author wukang
 */
interface RecyclerClickListener {
    /**
     * item 单击事件
     */
    fun onItemClick(v: View, position: Int, @LayoutRes layoutId: Int)

    /**
     * item 长按事件
     */
    fun onItemLongClick(v: View, position: Int, @LayoutRes layoutId: Int): Boolean
}