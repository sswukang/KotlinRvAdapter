package cn.wukang.library.adapter.itemtouch

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import cn.wukang.library.adapter.base.BaseViewHolder
import cn.wukang.library.listener.RecyclerClickListener

/**
 * 拖拽侧滑ViewHolder
 *
 * @author wukang
 */
open class ItemTouchViewHolder(root: View, @LayoutRes layoutId: Int, clickListener: RecyclerClickListener, private var listener: ItemViewStateChangeListener)
    : BaseViewHolder(root, layoutId, clickListener), ItemTouchCallBack.OnStateChangedListener {
    /**
     * 拖拽状态改变
     */
    interface ItemViewStateChangeListener {
        fun onItemPressed(itemView: View)

        fun onItemClear(itemView: View)
    }

    companion object {
        /**
         * 创建 ItemTouchViewHolder 的方法
         *
         * @param root          每一个条目的根view [RecyclerView.ViewHolder.itemView]
         * @param layoutId      该条目的layout id，常用于多条目的区分
         * @param clickListener [RecyclerClickListener]
         * @param listener      [ItemViewStateChangeListener]
         * @return [ItemTouchViewHolder]
         */
        fun get(root: View, @LayoutRes layoutId: Int, clickListener: RecyclerClickListener, listener: ItemViewStateChangeListener)
                : ItemTouchViewHolder = ItemTouchViewHolder(root, layoutId, clickListener, listener)
    }

    override fun onItemPressed() {
        listener.onItemPressed(itemView)
    }

    override fun onItemClear() {
        listener.onItemClear(itemView)
    }
}