package cn.wukang.library.adapter.base

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wukang.library.listener.RecyclerClickListener

/**
 * RecyclerView基础Adapter。
 *
 * @param layoutId adapter需要的布局资源id
 * @param data     数据
 * @author wukang
 */
abstract class BaseAdapter<T, H : BaseViewHolder>(@LayoutRes private var layoutId: Int, private var data: List<T>) : RecyclerView.Adapter<H>(), RecyclerClickListener {
    init {
        this.setHasStableIds(true)
    }

    fun getData(): List<T> = data

    fun setData(data: List<T>) {
        this.data = data
    }

    /**
     * @return 获得item数据总个数
     */
    fun getDataSize(): Int = getData().size

    /**
     * @param position item下标
     * @return 获得item数据封装
     */
    fun getDataItem(position: Int): T? {
        var index: Int = position
        val data: List<T> = getData()
        if (data.isNotEmpty()) {
            if (index >= data.size) {
                index %= data.size
            }
            return data[position]
        }
        return null
    }

    // 设置ID，保证item操作不错乱
    override fun getItemId(position: Int): Long = getDataItem(position)?.hashCode()?.toLong()
            ?: super.getItemId(position)

    /**
     * @return 设置item总个数（一般为数据总个数，设置成[Integer.MAX_VALUE]可实现无限轮播）
     */
    override fun getItemCount(): Int = getDataSize()

    /**
     * 利用getItemViewType传递layout id
     *
     * @param position 当前行数
     * @return layout id
     */
    @LayoutRes
    override fun getItemViewType(position: Int): Int = layoutId

    // 创建hold
    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H =
            BaseViewHolder.get(LayoutInflater.from(parent.context).inflate(viewType, parent, false), viewType, this) as H

    // 绑定hold
    override fun onBindViewHolder(holder: H, position: Int) = convert(position, getDataItem(position), holder)

    /**
     * 实现该抽象方法，完成数据的填充。
     *
     * @param position 当前item的position（无限轮播时会超过数据总个数）
     * @param t        position 对应的对象（无限轮播时为对数据总个数取余后对应的对象）
     * @param holder   [H]
     */
    abstract fun convert(position: Int, t: T?, holder: H)

    /**
     * 单击事件
     *
     * @param v 点击的item [BaseViewHolder.itemView]
     * @param position 当前点击的position，采用[BaseViewHolder.getLayoutPosition]（无限轮播时会超过数据总个数）
     * @param layoutId item布局id[BaseViewHolder.getLayoutId]
     */
    override fun onItemClick(v: View, position: Int, @LayoutRes layoutId: Int) {}

    /**
     * 长按事件
     *
     * @param v 点击的item [BaseViewHolder.itemView]
     * @param position 当前点击的position，采用[BaseViewHolder.getLayoutPosition]（无限轮播时会超过数据总个数）
     * @param layoutId item布局id[BaseViewHolder.getLayoutId]
     * @return 是否消费事件
     */
    override fun onItemLongClick(v: View, position: Int, @LayoutRes layoutId: Int): Boolean = false
}