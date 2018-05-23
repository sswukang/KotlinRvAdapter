package cn.wukang.library.adapter.multi

import android.support.annotation.LayoutRes
import android.view.View
import cn.wukang.library.adapter.base.BaseAdapter
import cn.wukang.library.adapter.base.BaseViewHolder

/**
 * multi Item Adapter。
 *
 * @param data 数据
 * @author wukang
 */
abstract class MultiAdapter<T>(data: List<T>) : BaseAdapter<T, BaseViewHolder>(-1, data) {
    /**
     * 利用getItemViewType传递layout id
     *
     * @param position 当前item的position（无限轮播时会超过数据总个数）
     * @return layout id
     */
    @LayoutRes
    override fun getItemViewType(position: Int): Int = getItemLayoutId(position, getDataItem(position))

    override fun convert(position: Int, t: T?, holder: BaseViewHolder) = convert(position, t, holder, holder.getLayoutId())

    override fun onItemClick(v: View, position: Int, @LayoutRes layoutId: Int) = onItemClick(v, position, getDataItem(position), layoutId)

    override fun onItemLongClick(v: View, position: Int, @LayoutRes layoutId: Int): Boolean = onItemLongClick(v, position, getDataItem(position), layoutId)

    /**
     * 实现该抽象方法，得到单个item的layout id。
     *
     * @param position 当前item的position（无限轮播时会超过数据总个数）
     * @param t        position 对应的对象（无限轮播时为对数据总个数取余后对应的对象）
     * @return layout id
     */
    abstract fun getItemLayoutId(position: Int, t: T?): Int

    /**
     * 实现该抽象方法，完成数据的填充。
     *
     * @param position 当前item的position（无限轮播时会超过数据总个数）
     * @param t        position 对应的对象（无限轮播时为对数据总个数取余后对应的对象）
     * @param holder   [BaseViewHolder]
     * @param layoutId 布局id (用于区别不同item)
     */
    abstract fun convert(position: Int, t: T?, holder: BaseViewHolder, @LayoutRes layoutId: Int)

    /**
     * item的单击事件
     *
     * @param v 触发点击事件的View
     * @param position 当前点击的position，采用[BaseViewHolder.getLayoutPosition]（无限轮播时会超过数据总个数）
     * @param t        position 对应的对象（无限轮播时为对数据总个数取余后对应的对象）
     * @param layoutId 布局id (用于区别不同item)
     */
    open fun onItemClick(v: View, position: Int, t: T?, @LayoutRes layoutId: Int) {}

    /**
     * item的长按事件
     *
     * @param v 触发点击事件的View
     * @param position 当前点击的position，采用[BaseViewHolder.getLayoutPosition]（无限轮播时会超过数据总个数）
     * @param t        position 对应的对象（无限轮播时为对数据总个数取余后对应的对象）
     * @param layoutId 布局id (用于区别不同item)
     * @return 长按事件是否被消费
     */
    open fun onItemLongClick(v: View, position: Int, t: T?, @LayoutRes layoutId: Int): Boolean = false
}