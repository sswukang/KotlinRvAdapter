package cn.wukang.library.adapter.itemtouch

import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wukang.library.adapter.base.BaseAdapter
import java.util.*

/**
 * 拖拽侧滑Adapter
 *
 * @param layoutId     adapter需要的布局资源id
 * @param data         数据
 * @param pressedColor 被选中时颜色
 * @param clearColor   清除时颜色
 * @author wukang
 */
abstract class ItemTouchAdapter<T>(@LayoutRes layoutId: Int, data: List<T>, @ColorInt private var pressedColor: Int = Color.GRAY, @ColorInt private var clearColor: Int = Color.TRANSPARENT)
    : BaseAdapter<T, ItemTouchViewHolder>(layoutId, data), ItemTouchCallBack.OnMoveSwipeListener, ItemTouchViewHolder.ItemViewStateChangeListener {
    /**
     * 设置item总个数（不允许设置无限轮播）
     */
    final override fun getItemCount(): Int = super.getItemCount()

    // 创建hold
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTouchViewHolder = ItemTouchViewHolder.get(
            LayoutInflater.from(parent.context).inflate(viewType, parent, false), viewType, this, this)

    final override fun onItemClick(v: View, position: Int, @LayoutRes layoutId: Int) = onItemClick(v, position, getDataItem(position))

    final override fun onItemLongClick(v: View, position: Int, @LayoutRes layoutId: Int): Boolean = onItemLongClick(v, position, getDataItem(position))

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        //交换数据源位置
        Collections.swap(getData(), fromPosition, toPosition)
        //交换列表中数据位置
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemSwipe(position: Int) {
        val data: MutableList<T> = getData().toMutableList()
        //删除数据源中对应数据
        data.removeAt(position)
        //删除列表中对应位置
        notifyItemRemoved(position)
    }

    /**
     * item的单击事件
     *
     * @param itemView 点击的item [ItemTouchViewHolder.itemView]
     * @param position 当前item的position
     * @param t        position 对应的对象
     */
    open fun onItemClick(itemView: View, position: Int, t: T?) {}

    /**
     * item的长按事件
     *
     * @param itemView 点击的item [ItemTouchViewHolder.itemView]
     * @param position 当前item的position
     * @param t        position 对应的对象
     * @return 长按事件是否被消费
     */
    open fun onItemLongClick(itemView: View, position: Int, t: T?): Boolean = false

    override fun onItemPressed(itemView: View) = itemView.setBackgroundColor(pressedColor)

    override fun onItemClear(itemView: View) = itemView.setBackgroundColor(clearColor)
}