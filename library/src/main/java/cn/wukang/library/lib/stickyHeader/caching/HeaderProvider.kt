package cn.wukang.library.lib.stickyHeader.caching

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Implemented by objects that provide header views for decoration
 */
interface HeaderProvider {
    /**
     * Will provide a header view for a given position in the RecyclerView
     *
     * @param recyclerView that will display the header
     * @param position     that will be headed by the header
     * @return a header view for the given position and list
     */
    fun getHeader(recyclerView: RecyclerView, position: Int): View

    /**
     * describe this functionality and its necessity
     */
    fun invalidate()
}