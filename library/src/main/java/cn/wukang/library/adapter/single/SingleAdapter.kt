package cn.wukang.library.adapter.single

import android.support.annotation.LayoutRes
import android.view.View
import cn.wukang.library.adapter.base.BaseAdapter
import cn.wukang.library.adapter.base.BaseViewHolder

/**
 * single item Adapter
 *
 * @param layoutId adapter需要的布局资源id
 * @param data     数据
 * @author wukang
 */
abstract class SingleAdapter<T>(layoutId: Int, data: List<T>) : BaseAdapter<T, BaseViewHolder>(layoutId, data) {
    override fun onItemClick(v: View, position: Int, @LayoutRes layoutId: Int) = onItemClick(v, position, getDataItem(position))

    override fun onItemLongClick(v: View, position: Int, @LayoutRes layoutId: Int): Boolean = onItemLongClick(v, position, getDataItem(position))

    /**
     * item的单击事件
     *
     * @param v 点击的item [BaseViewHolder.itemView]
     * @param position 当前点击的position，采用[BaseViewHolder.getLayoutPosition]（无限轮播时会超过数据总个数）
     * @param t        position 对应的对象（无限轮播时为对数据总个数取余后对应的对象）
     */
    open fun onItemClick(v: View, position: Int, t: T?) {}

    /**
     * item的长按事件
     *
     * @param v 点击的item [BaseViewHolder.itemView]
     * @param position 当前点击的position，采用[BaseViewHolder.getLayoutPosition]（无限轮播时会超过数据总个数）
     * @param t        position 对应的对象（无限轮播时为对数据总个数取余后对应的对象）
     * @return 长按事件是否被消费
     */
    open fun onItemLongClick(v: View, position: Int, t: T?): Boolean = false
}