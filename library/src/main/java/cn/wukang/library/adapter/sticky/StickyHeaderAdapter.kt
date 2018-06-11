package cn.wukang.library.adapter.sticky

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.wukang.library.adapter.base.BaseViewHolder
import cn.wukang.library.adapter.single.SingleAdapter
import cn.wukang.library.lib.stickyHeader.sticky.StickyRecyclerHeadersAdapter

/**
 * 粘性头部适配器
 *
 * @param headerLayoutId header需要的布局资源id
 * @param layoutId       content需要的布局资源id
 * @param data           数据
 * @author wukang
 */
abstract class StickyHeaderAdapter<T>(@LayoutRes private var headerLayoutId: Int, @LayoutRes layoutId: Int, data: List<T>)
    : SingleAdapter<T>(layoutId, data), StickyRecyclerHeadersAdapter<BaseViewHolder> {
    /**
     * 设置item总个数（不允许设置无限轮播）
     */
    final override fun getItemCount(): Int = super.getItemCount()

    override fun getHeaderId(position: Int): Long = getHeaderId(position, getDataItem(position))

    override fun onCreateHeaderViewHolder(parent: ViewGroup): BaseViewHolder = BaseViewHolder.get(
            LayoutInflater.from(parent.context).inflate(headerLayoutId, parent, false), headerLayoutId, this)

    override fun onBindHeaderViewHolder(holder: BaseViewHolder, position: Int) = convertHeader(position, getDataItem(position), holder)

    /**
     * 设置粘性头部高度，方便sticky header定位
     */
    abstract val headerHeight: Int

    /**
     * 获得 header id 。如果某几个条目有相同的header，其id 需相同。
     * 如某条目不需要header，则return < 0 即可。
     * 例：字符串可以用 String.charAt(0)
     *
     * @param position 当前item的position
     * @param t        position 对应的对象
     * @return header id [StickyRecyclerHeadersAdapter.getHeaderId]
     */
    abstract fun getHeaderId(position: Int, t: T?): Long

    /**
     * 填充粘性头部显示的内容
     *
     * @param position header 条目下标
     * @param t        header 对象数据封装
     * @param holder   [BaseViewHolder]
     */
    abstract fun convertHeader(position: Int, t: T?, holder: BaseViewHolder)
}