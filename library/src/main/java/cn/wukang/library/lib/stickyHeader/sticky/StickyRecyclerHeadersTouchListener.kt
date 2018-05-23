package cn.wukang.library.lib.stickyHeader.sticky

import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View

/**
 * StickyRecyclerHeadersTouchListener
 */
class StickyRecyclerHeadersTouchListener<VH : RecyclerView.ViewHolder>(
        private var recyclerView: RecyclerView, private var decoration: StickyRecyclerHeadersDecoration<VH>) : RecyclerView.OnItemTouchListener {
    private val mTapDetector: GestureDetector by lazy { GestureDetector(recyclerView.context, SingleTapDetector()) }

    private var onHeaderClickListener: OnHeaderClickListener? = null

    fun setOnHeaderClickListener(listener: OnHeaderClickListener) {
        onHeaderClickListener = listener
    }

    fun getAdapter(): StickyRecyclerHeadersAdapter<*> {
        val adapter: RecyclerView.Adapter<*> = recyclerView.adapter
        return if (adapter is StickyRecyclerHeadersAdapter<*>) {
            adapter
        } else {
            throw IllegalStateException("A RecyclerView with " +
                    StickyRecyclerHeadersTouchListener::class.java.simpleName +
                    " requires a " + StickyRecyclerHeadersAdapter::class.java.simpleName)
        }
    }

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        if (this.onHeaderClickListener != null) {
            val tapDetectorResponse: Boolean = mTapDetector.onTouchEvent(e)
            if (tapDetectorResponse) {
                // Don't return false if a single tap is detected
                return true
            }
            if (e.action == MotionEvent.ACTION_DOWN) {
                val position = decoration.findHeaderPositionUnder(e.x.toInt(), e.y.toInt())
                return position != -1
            }
        }
        return false
    }

    override fun onTouchEvent(view: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    private inner class SingleTapDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val position = decoration.findHeaderPositionUnder(e.x.toInt(), e.y.toInt())
            if (position != -1) {
                val headerView = decoration.getHeaderView(recyclerView, position)
                val headerId = getAdapter().getHeaderId(position)
                onHeaderClickListener?.onHeaderClick(headerView, position, headerId)
                recyclerView.playSoundEffect(SoundEffectConstants.CLICK)
                headerView.onTouchEvent(e)
                return true
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean = true
    }

    interface OnHeaderClickListener {
        fun onHeaderClick(header: View, position: Int, headerId: Long)
    }
}