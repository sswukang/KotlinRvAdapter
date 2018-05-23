package cn.wukang.library.lib.stickyHeader.sticky

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import cn.wukang.library.lib.stickyHeader.caching.HeaderProvider
import cn.wukang.library.lib.stickyHeader.caching.HeaderViewCache
import cn.wukang.library.lib.stickyHeader.calculation.DimensionCalculator
import cn.wukang.library.lib.stickyHeader.rendering.HeaderRenderer
import cn.wukang.library.lib.stickyHeader.util.LinearLayoutOrientationProvider
import cn.wukang.library.lib.stickyHeader.util.OrientationProvider

/**
 * StickyRecyclerHeadersDecoration
 */
class StickyRecyclerHeadersDecoration<VH : RecyclerView.ViewHolder> private constructor(
        private var adapter: StickyRecyclerHeadersAdapter<VH>, private var visibilityAdapter: ItemVisibilityAdapter?,
        private var orientationProvider: OrientationProvider, private var dimensionCalculator: DimensionCalculator,
        private var headerRenderer: HeaderRenderer, private var headerProvider: HeaderProvider,
        private var headerPositionCalculator: HeaderPositionCalculator<VH>) : RecyclerView.ItemDecoration() {

    //  Consider passing in orientation to simplify orientation accounting within calculation
    constructor (adapter: StickyRecyclerHeadersAdapter<VH>) : this(adapter, null)

    constructor(adapter: StickyRecyclerHeadersAdapter<VH>, visibilityAdapter: ItemVisibilityAdapter?) : this(
            adapter, visibilityAdapter, LinearLayoutOrientationProvider(), DimensionCalculator())

    private constructor(adapter: StickyRecyclerHeadersAdapter<VH>, visibilityAdapter: ItemVisibilityAdapter?,
                        orientationProvider: OrientationProvider, dimensionCalculator: DimensionCalculator)
            : this(adapter, visibilityAdapter, orientationProvider, dimensionCalculator,
            HeaderRenderer(orientationProvider), HeaderViewCache<VH>(adapter, orientationProvider))

    private constructor(adapter: StickyRecyclerHeadersAdapter<VH>, visibilityAdapter: ItemVisibilityAdapter?,
                        orientationProvider: OrientationProvider, dimensionCalculator: DimensionCalculator,
                        headerRenderer: HeaderRenderer, headerProvider: HeaderProvider)
            : this(adapter, visibilityAdapter, orientationProvider, dimensionCalculator, headerRenderer, headerProvider,
            HeaderPositionCalculator<VH>(adapter, orientationProvider, headerProvider, dimensionCalculator))

    private val mHeaderRectArray: SparseArray<Rect> by lazy { SparseArray<Rect>() }
    /**
     * The following field is used as a buffer for internal calculations. Its sole purpose is to avoid
     * allocating new Rect every time we need one.
     */
    private val mTempRect: Rect by lazy { Rect() }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition: Int = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }
        if (headerPositionCalculator.hasNewHeader(itemPosition, orientationProvider.isReverseLayout(parent))) {
            val header: View = getHeaderView(parent, itemPosition)
            setItemOffsetsForHeader(outRect, header, orientationProvider.getOrientation(parent))
        }
    }

    /**
     * Sets the offsets for the first item in a section to make room for the header view
     *
     * @param itemOffsets rectangle to define offsets for the item
     * @param header      view used to calculate offset for the item
     * @param orientation used to calculate offset for the item
     */
    private fun setItemOffsetsForHeader(itemOffsets: Rect, header: View, orientation: Int) {
        dimensionCalculator.initMargins(mTempRect, header)
        if (orientation == LinearLayoutManager.VERTICAL) {
            itemOffsets.top = header.height + mTempRect.top + mTempRect.bottom
        } else {
            itemOffsets.left = header.width + mTempRect.left + mTempRect.right
        }
    }

    /**
     * Gets the header view for the associated position.  If it doesn't exist yet, it will be
     * created, measured, and laid out.
     *
     * @param parent   the recyclerview
     * @param position the position to get the header view for
     * @return Header view
     */
    fun getHeaderView(parent: RecyclerView, position: Int): View = headerProvider.getHeader(parent, position)

    /**
     * Invalidates cached headers.  This does not invalidate the recyclerview, you should do that manually after
     * calling this method.
     */
    fun invalidateHeaders() {
        headerProvider.invalidate()
        mHeaderRectArray.clear()
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDrawOver(canvas, parent, state)

        val childCount: Int = parent.childCount
        if (childCount <= 0 || adapter.getItemCount() <= 0) {
            return
        }

        for (i: Int in 0 until childCount) {
            val itemView: View = parent.getChildAt(i)
            val position: Int = parent.getChildAdapterPosition(itemView)
            if (position == RecyclerView.NO_POSITION) {
                continue
            }

            val hasStickyHeader: Boolean = headerPositionCalculator.hasStickyHeader(itemView, orientationProvider.getOrientation(parent), position)
            if (hasStickyHeader || headerPositionCalculator.hasNewHeader(position, orientationProvider.isReverseLayout(parent))) {
                val header: View = headerProvider.getHeader(parent, position)
                //re-use existing Rect, if any.
                var headerOffset: Rect? = mHeaderRectArray.get(position)
                if (headerOffset == null) {
                    headerOffset = Rect()
                    mHeaderRectArray.put(position, headerOffset)
                }
                headerPositionCalculator.initHeaderBounds(headerOffset, parent, header, itemView, hasStickyHeader)
                headerRenderer.drawHeader(parent, canvas, header, headerOffset)
            }
        }
    }

    /**
     * Gets the position of the header under the specified (x, y) coordinates.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return position of header, or -1 if not found
     */
    fun findHeaderPositionUnder(x: Int, y: Int): Int {
        for (i: Int in 0 until mHeaderRectArray.size()) {
            val rect: Rect = mHeaderRectArray.get(mHeaderRectArray.keyAt(i))
            if (rect.contains(x, y)) {
                val position: Int = mHeaderRectArray.keyAt(i)
                if (visibilityAdapter?.isPositionVisible(position) != false) {
                    return position
                }
            }
        }
        return -1
    }
}