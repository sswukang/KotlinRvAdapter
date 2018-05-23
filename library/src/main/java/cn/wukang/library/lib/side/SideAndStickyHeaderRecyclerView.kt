package cn.wukang.library.lib.side

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.ColorInt
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import cn.wukang.library.adapter.base.BaseViewHolder
import cn.wukang.library.adapter.sticky.StickyHeaderAdapter
import cn.wukang.library.lib.stickyHeader.sticky.StickyRecyclerHeadersDecoration

/**
 * 选择条结合粘性头部的RecyclerView
 *
 * @author wukang
 */
class SideAndStickyHeaderRecyclerView : FrameLayout {

    private var recyclerView: RecyclerView
    private var sideBar: SideBar
    /**
     * sticky header 目前需要线性布局
     */
    private var linearLayoutManager: LinearLayoutManager? = null
    /**
     * 利用decoration添加header
     */
    private var decoration: StickyRecyclerHeadersDecoration<BaseViewHolder>? = null
    /**
     * 滑动监听，联动wave side
     */
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    /**
     * recycler view 是否正在滚动
     */
    private var isMove: Boolean = false
    /**
     * move到哪个条目
     */
    private var movePosition: Int = 0
    /**
     * 是否平滑滚动
     */
    private var isSmooth: Boolean = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        recyclerView = RecyclerView(context)
        recyclerView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(recyclerView)
        sideBar = SideBar(context)
        sideBar.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        sideBar.setPadding(0, 0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt(), 0)
        addView(sideBar)
        linkageMove(false)
    }

    /**
     * 支持设置sticky header adapter
     *
     * @param adapter [StickyHeaderAdapter]
     */
    fun <T> setStickyHeaderAdapter(adapter: StickyHeaderAdapter<T>) {
        if (linearLayoutManager == null)
            linearLayoutManager = LinearLayoutManager(context)
        if (decoration == null)
            decoration = StickyRecyclerHeadersDecoration(adapter)
        if (onScrollListener == null)
            onScrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        //在这里进行第二次滚动
                        if (isMove) {
                            isMove = false
                            //获取要置顶的项在当前屏幕的位置，movePosition是记录要置顶项在RecyclerView中的位置
                            val n: Int = movePosition - (linearLayoutManager?.findFirstVisibleItemPosition() ?: 0)
                            if (0 <= n && n < recyclerView.childCount) {
                                //获取要置顶的项顶部离RecyclerView顶部的距离
                                val top: Int = recyclerView.getChildAt(n).top - adapter.getHeaderHeight()
                                //最后的移动
                                if (isSmooth)
                                    recyclerView.smoothScrollBy(0, top)
                                else
                                    recyclerView.scrollBy(0, top)
                            }
                        }
                    }
                }
            }

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addItemDecoration(decoration)
        recyclerView.addOnScrollListener(onScrollListener)
        recyclerView.adapter = adapter
    }

    /**
     * 关联wave side移动到指定条目
     *
     * @param position 目标条目
     */
    fun moveToPosition(position: Int) {
        movePosition = position
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        val firstItem: Int = linearLayoutManager?.findFirstVisibleItemPosition() ?: 0
        val lastItem: Int = linearLayoutManager?.findLastVisibleItemPosition() ?: 0
        //然后区分情况
        if (position <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            if (isSmooth)
                recyclerView.smoothScrollToPosition(position)
            else
                recyclerView.scrollToPosition(position)
        } else if (position <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            val top: Int = recyclerView.getChildAt(position - firstItem).top
            if (isSmooth)
                recyclerView.smoothScrollBy(0, top)
            else
                recyclerView.scrollBy(0, top)
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            if (isSmooth)
                recyclerView.smoothScrollToPosition(position)
            else
                recyclerView.scrollToPosition(position)
            //这里这个变量是用在RecyclerView滚动监听里面的
            isMove = true
        }
    }

    private fun setSmooth(smooth: Boolean) {
        this.isSmooth = smooth
    }

    private fun setLazyRespond(lazyRespond: Boolean) {
        sideBar.setLazyRespond(lazyRespond)
    }

    fun linkageMove(linkageMove: Boolean) {
        setLazyRespond(!linkageMove)
        setSmooth(!linkageMove)
    }

    fun setIndexItems(vararg indexItems: String) {
        sideBar.setIndexItems(*indexItems)
    }

    fun setIndexItems(indexItems: List<String>) {
        sideBar.setIndexItems(*indexItems.toTypedArray())
    }

    fun setTextColor(@ColorInt color: Int) {
        sideBar.setTextColor(color)
    }

    fun setPosition(position: Int) {
        sideBar.setPosition(position)
    }

    fun setMaxOffset(offset: Int) {
        sideBar.setMaxOffset(offset)
    }

    fun setTextAlign(align: Int) {
        sideBar.setTextAlign(align)
    }

    fun setOnSelectIndexItemListener(onSelectIndexItemListener: SideBar.OnSelectIndexItemListener) {
        sideBar.setOnSelectIndexItemListener(onSelectIndexItemListener)
    }

    fun setItemAnimator(animator: RecyclerView.ItemAnimator) {
        recyclerView.itemAnimator = animator
    }

    fun addItemDecoration(decor: RecyclerView.ItemDecoration) {
        recyclerView.addItemDecoration(decor)
    }
}