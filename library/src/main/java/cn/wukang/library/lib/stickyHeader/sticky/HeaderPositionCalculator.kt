package cn.wukang.library.lib.stickyHeader.sticky

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import cn.wukang.library.lib.stickyHeader.caching.HeaderProvider
import cn.wukang.library.lib.stickyHeader.calculation.DimensionCalculator
import cn.wukang.library.lib.stickyHeader.util.OrientationProvider

/**
 * Calculates the position and location of header views
 */
internal class HeaderPositionCalculator<VH : RecyclerView.ViewHolder>(
        private var adapter: StickyRecyclerHeadersAdapter<VH>, private var orientationProvider: OrientationProvider,
        private var headerProvider: HeaderProvider, private var dimensionCalculator: DimensionCalculator) {

    /**
     * The following fields are used as buffers for internal calculations. Their sole purpose is to avoid
     * allocating new Rect every time we need one.
     */
    private val mTempRect1: Rect by lazy { Rect() }
    private val mTempRect2: Rect by lazy { Rect() }

    /**
     * Determines if a view should have a sticky header.
     * The view has a sticky header if:
     * 1. It is the first element in the recycler view
     * 2. It has a valid ID associated to its position
     *
     * @param itemView    given by the RecyclerView
     * @param orientation of the Recyclerview
     * @param position    of the list item in question
     * @return True if the view should have a sticky header
     */
    fun hasStickyHeader(itemView: View, orientation: Int, position: Int): Boolean {
        val offset: Int
        val margin: Int
        dimensionCalculator.initMargins(mTempRect1, itemView)
        if (orientation == LinearLayout.VERTICAL) {
            offset = itemView.top
            margin = mTempRect1.top
        } else {
            offset = itemView.left
            margin = mTempRect1.left
        }

        return offset <= margin && adapter.getHeaderId(position) >= 0
    }

    /**
     * Determines if an item in the list should have a header that is different than the item in the
     * list that immediately precedes it. Items with no headers will always return false.
     *
     * @param position        of the list item in questions
     * @param isReverseLayout TRUE if layout manager has flag isReverseLayout
     * @return true if this item has a different header than the previous item in the list
     */
    fun hasNewHeader(position: Int, isReverseLayout: Boolean): Boolean {
        if (indexOutOfBounds(position)) {
            return false
        }

        val headerId: Long = adapter.getHeaderId(position)

        if (headerId < 0) {
            return false
        }

        var nextItemHeaderId: Long = -1
        val nextItemPosition: Int = position + if (isReverseLayout) 1 else -1
        if (!indexOutOfBounds(nextItemPosition)) {
            nextItemHeaderId = adapter.getHeaderId(nextItemPosition)
        }
        val firstItemPosition: Int = if (isReverseLayout) adapter.getItemCount() - 1 else 0

        return position == firstItemPosition || headerId != nextItemHeaderId
    }

    private fun indexOutOfBounds(position: Int): Boolean = position < 0 || position >= adapter.getItemCount()

    fun initHeaderBounds(bounds: Rect, recyclerView: RecyclerView, header: View, firstView: View, firstHeader: Boolean) {
        val orientation: Int = orientationProvider.getOrientation(recyclerView)
        initDefaultHeaderOffset(bounds, recyclerView, header, firstView, orientation)

        if (firstHeader && isStickyHeaderBeingPushedOffscreen(recyclerView, header)) {
            val viewAfterNextHeader: View? = getFirstViewUnobscuredByHeader(recyclerView, header)
            val firstViewUnderHeaderPosition: Int = recyclerView.getChildAdapterPosition(viewAfterNextHeader)
            val secondHeader: View = headerProvider.getHeader(recyclerView, firstViewUnderHeaderPosition)
            translateHeaderWithNextHeader(recyclerView, orientationProvider.getOrientation(recyclerView), bounds,
                    header, viewAfterNextHeader, secondHeader)
        }
    }

    private fun initDefaultHeaderOffset(headerMargins: Rect, recyclerView: RecyclerView, header: View, firstView: View, orientation: Int) {
        val translationX: Int
        val translationY: Int
        dimensionCalculator.initMargins(mTempRect1, header)

        val layoutParams: ViewGroup.LayoutParams = firstView.layoutParams
        var leftMargin = 0
        var topMargin = 0
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            leftMargin = layoutParams.leftMargin
            topMargin = layoutParams.topMargin
        }

        if (orientation == LinearLayoutManager.VERTICAL) {
            translationX = firstView.left - leftMargin + mTempRect1.left
            translationY = Math.max(firstView.top - topMargin - header.height - mTempRect1.bottom, getListTop(recyclerView) + mTempRect1.top)
        } else {
            translationY = firstView.top - topMargin + mTempRect1.top
            translationX = Math.max(firstView.left - leftMargin - header.width - mTempRect1.right, getListLeft(recyclerView) + mTempRect1.left)
        }

        headerMargins.set(translationX, translationY, translationX + header.width, translationY + header.height)
    }

    private fun isStickyHeaderBeingPushedOffscreen(recyclerView: RecyclerView, stickyHeader: View): Boolean {
        val viewAfterHeader: View? = getFirstViewUnobscuredByHeader(recyclerView, stickyHeader)
        val firstViewUnderHeaderPosition: Int = recyclerView.getChildAdapterPosition(viewAfterHeader)
        if (firstViewUnderHeaderPosition == RecyclerView.NO_POSITION) {
            return false
        }

        val isReverseLayout: Boolean = orientationProvider.isReverseLayout(recyclerView)
        if (firstViewUnderHeaderPosition > 0 && hasNewHeader(firstViewUnderHeaderPosition, isReverseLayout)) {
            val nextHeader: View = headerProvider.getHeader(recyclerView, firstViewUnderHeaderPosition)
            dimensionCalculator.initMargins(mTempRect1, nextHeader)
            dimensionCalculator.initMargins(mTempRect2, stickyHeader)

            if (viewAfterHeader != null) {
                return if (orientationProvider.getOrientation(recyclerView) == LinearLayoutManager.VERTICAL) {
                    val topOfNextHeader: Int = viewAfterHeader.top - mTempRect1.bottom - nextHeader.height - mTempRect1.top
                    val bottomOfThisHeader: Int = recyclerView.paddingTop + stickyHeader.bottom + mTempRect2.top + mTempRect2.bottom
                    topOfNextHeader < bottomOfThisHeader
                } else {
                    val leftOfNextHeader: Int = viewAfterHeader.left - mTempRect1.right - nextHeader.width - mTempRect1.left
                    val rightOfThisHeader: Int = recyclerView.paddingLeft + stickyHeader.right + mTempRect2.left + mTempRect2.right
                    leftOfNextHeader < rightOfThisHeader
                }
            }
        }

        return false
    }

    private fun translateHeaderWithNextHeader(recyclerView: RecyclerView, orientation: Int, translation: Rect,
                                              currentHeader: View, viewAfterNextHeader: View?, nextHeader: View) {
        dimensionCalculator.initMargins(mTempRect1, nextHeader)
        dimensionCalculator.initMargins(mTempRect2, currentHeader)
        if (orientation == LinearLayoutManager.VERTICAL) {
            val topOfStickyHeader: Int = getListTop(recyclerView) + mTempRect2.top + mTempRect2.bottom
            val shiftFromNextHeader: Int = (viewAfterNextHeader?.top ?: 0) - nextHeader.height -
                    mTempRect1.bottom - mTempRect1.top - currentHeader.height - topOfStickyHeader
            if (shiftFromNextHeader < topOfStickyHeader) {
                translation.top += shiftFromNextHeader
            }
        } else {
            val leftOfStickyHeader: Int = getListLeft(recyclerView) + mTempRect2.left + mTempRect2.right
            val shiftFromNextHeader: Int = (viewAfterNextHeader?.left ?: 0) - nextHeader.width -
                    mTempRect1.right - mTempRect1.left - currentHeader.width - leftOfStickyHeader
            if (shiftFromNextHeader < leftOfStickyHeader) {
                translation.left += shiftFromNextHeader
            }
        }
    }

    /**
     * Returns the first item currently in the RecyclerView that is not obscured by a header.
     *
     * @param parent Recyclerview containing all the list items
     * @return first item that is fully beneath a header
     */
    private fun getFirstViewUnobscuredByHeader(parent: RecyclerView, firstHeader: View): View? {
        val isReverseLayout: Boolean = orientationProvider.isReverseLayout(parent)
        val step: Int = if (isReverseLayout) -1 else 1
        val from: Int = if (isReverseLayout) parent.childCount - 1 else 0
        var i: Int = from
        while (i >= 0 && i <= parent.childCount - 1) {
            val child: View = parent.getChildAt(i)
            if (!itemIsObscuredByHeader(parent, child, firstHeader, orientationProvider.getOrientation(parent))) {
                return child
            }
            i += step
        }
        return null
    }

    /**
     * Determines if an item is obscured by a header
     *
     * @param parent      recycler view
     * @param item        to determine if obscured by header
     * @param header      that might be obscuring the item
     * @param orientation of the [RecyclerView]
     * @return true if the item view is obscured by the header view
     */
    private fun itemIsObscuredByHeader(parent: RecyclerView, item: View, header: View, orientation: Int): Boolean {
        val layoutParams: RecyclerView.LayoutParams = item.layoutParams as RecyclerView.LayoutParams
        dimensionCalculator.initMargins(mTempRect1, header)

        val adapterPosition: Int = parent.getChildAdapterPosition(item)
        if (adapterPosition == RecyclerView.NO_POSITION || headerProvider.getHeader(parent, adapterPosition) !== header) {
            // Resolves https://github.com/timehop/sticky-headers-recyclerview/issues/36
            // Handles an edge case where a trailing header is smaller than the current sticky header.
            return false
        }

        return if (orientation == LinearLayoutManager.VERTICAL) {
            val itemTop: Int = item.top - layoutParams.topMargin
            val headerBottom: Int = getListTop(parent) + header.bottom + mTempRect1.bottom + mTempRect1.top
            itemTop < headerBottom
        } else {
            val itemLeft: Int = item.left - layoutParams.leftMargin
            val headerRight: Int = getListLeft(parent) + header.right + mTempRect1.right + mTempRect1.left
            itemLeft < headerRight
        }
    }

    private fun getListTop(view: RecyclerView): Int = if (view.layoutManager.clipToPadding) view.paddingTop else 0

    private fun getListLeft(view: RecyclerView): Int = if (view.layoutManager.clipToPadding) view.paddingLeft else 0
}