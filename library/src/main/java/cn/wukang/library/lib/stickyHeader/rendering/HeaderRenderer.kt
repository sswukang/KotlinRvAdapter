package cn.wukang.library.lib.stickyHeader.rendering

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import cn.wukang.library.lib.stickyHeader.calculation.DimensionCalculator
import cn.wukang.library.lib.stickyHeader.util.OrientationProvider

/**
 * Responsible for drawing headers to the canvas provided by the item decoration
 */
class HeaderRenderer(private var orientationProvider: OrientationProvider, private var dimensionCalculator: DimensionCalculator) {

    constructor(orientationProvider: OrientationProvider) : this(orientationProvider, DimensionCalculator)

    /**
     * The following field is used as a buffer for internal calculations. Its sole purpose is to avoid
     * allocating new Rect every time we need one.
     */
    private val mTempRect: Rect by lazy { Rect() }

    /**
     * Draws a header to a canvas, offsetting by some x and y amount
     *
     * @param recyclerView the parent recycler view for drawing the header into
     * @param canvas       the canvas on which to draw the header
     * @param header       the view to draw as the header
     * @param offset       a Rect used to define the x/y offset of the header. Specify x/y offset by setting
     * the [Rect.left] and [Rect.top] properties, respectively.
     */
    fun drawHeader(recyclerView: RecyclerView, canvas: Canvas, header: View, offset: Rect) {
        canvas.save()

        if (recyclerView.layoutManager.clipToPadding) {
            // Clip drawing of headers to the padding of the RecyclerView. Avoids drawing in the padding
            initClipRectForHeader(mTempRect, recyclerView, header)
            canvas.clipRect(mTempRect)
        }

        canvas.translate(offset.left.toFloat(), offset.top.toFloat())

        header.draw(canvas)
        canvas.restore()
    }

    /**
     * Initializes a clipping rect for the header based on the margins of the header and the padding of the
     * recycler.
     * FIXME: Currently right margin in VERTICAL orientation and bottom margin in HORIZONTAL
     * orientation are clipped so they look accurate, but the headers are not being drawn at the
     * correctly smaller width and height respectively.
     *
     * @param clipRect     [Rect] for clipping a provided header to the padding of a recycler view
     * @param recyclerView for which to provide a header
     * @param header       for clipping
     */
    private fun initClipRectForHeader(clipRect: Rect, recyclerView: RecyclerView, header: View) {
        dimensionCalculator.initMargins(clipRect, header)
        if (orientationProvider.getOrientation(recyclerView) == LinearLayout.VERTICAL) {
            clipRect.set(
                    recyclerView.paddingLeft,
                    recyclerView.paddingTop,
                    recyclerView.width - recyclerView.paddingRight - clipRect.right,
                    recyclerView.height - recyclerView.paddingBottom)
        } else {
            clipRect.set(
                    recyclerView.paddingLeft,
                    recyclerView.paddingTop,
                    recyclerView.width - recyclerView.paddingRight,
                    recyclerView.height - recyclerView.paddingBottom - clipRect.bottom)
        }
    }
}