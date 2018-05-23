package cn.wukang.library.lib.stickyHeader.caching

import android.support.v4.util.LongSparseArray
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import cn.wukang.library.lib.stickyHeader.sticky.StickyRecyclerHeadersAdapter
import cn.wukang.library.lib.stickyHeader.util.OrientationProvider

/**
 * An implementation of [HeaderProvider] that creates and caches header views
 */
class HeaderViewCache<VH : RecyclerView.ViewHolder>(private var adapter: StickyRecyclerHeadersAdapter<VH>, private var provider: OrientationProvider) : HeaderProvider {

    private val headerViews: LongSparseArray<View> by lazy { LongSparseArray<View>() }

    override fun getHeader(recyclerView: RecyclerView, position: Int): View {
        val headerId: Long = adapter.getHeaderId(position)

        var header: View? = headerViews.get(headerId)
        if (header == null) {
            val viewHolder: VH = adapter.onCreateHeaderViewHolder(recyclerView)
            adapter.onBindHeaderViewHolder(viewHolder, position)
            header = viewHolder.itemView
            if (header.layoutParams == null) {
                header.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            val widthSpec: Int
            val heightSpec: Int

            if (provider.getOrientation(recyclerView) == LinearLayoutManager.VERTICAL) {
                widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY)
                heightSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.height, View.MeasureSpec.UNSPECIFIED)
            } else {
                widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.UNSPECIFIED)
                heightSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.height, View.MeasureSpec.EXACTLY)
            }

            val childWidth: Int = ViewGroup.getChildMeasureSpec(widthSpec, recyclerView.paddingLeft + recyclerView.paddingRight, header.layoutParams.width)
            val childHeight: Int = ViewGroup.getChildMeasureSpec(heightSpec, recyclerView.paddingTop + recyclerView.paddingBottom, header.layoutParams.height)
            header.measure(childWidth, childHeight)
            header.layout(0, 0, header.measuredWidth, header.measuredHeight)
            headerViews.put(headerId, header)
        }
        return header!!
    }

    override fun invalidate() = headerViews.clear()
}