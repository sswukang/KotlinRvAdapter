package cn.wukang.library.lib.stickyHeader.util

import android.support.v7.widget.RecyclerView

/**
 * Interface for getting the orientation of a RecyclerView from its LayoutManager
 */
interface OrientationProvider {
    fun getOrientation(recyclerView: RecyclerView): Int

    fun isReverseLayout(recyclerView: RecyclerView): Boolean
}