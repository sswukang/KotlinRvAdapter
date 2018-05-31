package cn.wukang.library.lib.stickyHeader.calculation

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup

/**
 * Helper to calculate various view dimensions
 */
object DimensionCalculator {
    /**
     * Populates [Rect] with margins for any view.
     *
     * @param margins rect to populate
     * @param view    for which to get margins
     */
    fun initMargins(margins: Rect, view: View) {
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams

        if (layoutParams is ViewGroup.MarginLayoutParams) {
            initMarginRect(margins, layoutParams)
        } else {
            margins.set(0, 0, 0, 0)
        }
    }

    /**
     * Converts [ViewGroup.MarginLayoutParams] into a representative [Rect].
     *
     * @param marginRect         Rect to be initialized with margins coordinates, where
     * [ViewGroup.MarginLayoutParams.leftMargin] is equivalent to [Rect.left], etc.
     * @param marginLayoutParams margins to populate the Rect with
     */
    private fun initMarginRect(marginRect: Rect, marginLayoutParams: ViewGroup.MarginLayoutParams) = marginRect.set(
            marginLayoutParams.leftMargin,
            marginLayoutParams.topMargin,
            marginLayoutParams.rightMargin,
            marginLayoutParams.bottomMargin
    )
}