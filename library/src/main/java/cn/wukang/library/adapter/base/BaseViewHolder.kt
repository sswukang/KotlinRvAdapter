package cn.wukang.library.adapter.base

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.wukang.library.listener.DebouncingOnClickListener
import cn.wukang.library.listener.RecyclerClickListener

/**
 * 自定义 RecyclerView 的 ViewHolder
 *
 * @author wukang
 */
open class BaseViewHolder internal constructor(root: View, @LayoutRes private var layoutId: Int, listener: RecyclerClickListener)
    : RecyclerView.ViewHolder(root) {
    private val views: SparseArray<View> = SparseArray()

    init {
        //添加监听事件
        itemView.setOnClickListener(object : DebouncingOnClickListener() {
            override fun doClick(v: View) = listener.onItemClick(v, layoutPosition, layoutId)
        })
        itemView.setOnLongClickListener { listener.onItemLongClick(it, layoutPosition, layoutId) }
    }

    companion object {
        /**
         * 自定义ViewHolder创建方法
         *
         * @param root     每一个条目的根view [RecyclerView.ViewHolder.itemView]
         * @param layoutId 该条目的layout id，可用于多条目的区分
         * @param listener 条目的监听
         * @return [BaseViewHolder]
         */
        fun get(root: View, @LayoutRes layoutId: Int, listener: RecyclerClickListener): BaseViewHolder = BaseViewHolder(root, layoutId, listener)
    }


    /**
     * 得到view
     *
     * @param viewId view在当前layout里设置的id
     * @param <T>    view的子类型
     * @return view的子类型实例 </T>
     * */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> getView(@IdRes viewId: Int): T {
        var view: View? = views.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T
    }

    /**
     * 获得item布局资源id（可用于multi adapter里区别不同item）
     *
     * @return item view res id
     */
    @LayoutRes
    fun getLayoutId(): Int = layoutId

    /**
     * 获得context，建议布局里使用用此方法得到context。
     *
     * @return [Context]
     */
    fun getContext(): Context = itemView.context

    /**
     * @return [ContextCompat.getColor]
     */
    @ColorInt
    fun getColor(@ColorRes resId: Int): Int = ContextCompat.getColor(getContext(), resId)

    /**
     * @return [ContextCompat.getDrawable]
     */
    fun getDrawable(@DrawableRes resId: Int): Drawable? = ContextCompat.getDrawable(getContext(), resId)

    /**
     * @return [Context.getString]
     */
    fun getString(@StringRes resId: Int): String = getContext().getString(resId)

    /**
     * @return [Context.getString]
     */
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String = getContext().getString(resId, *formatArgs)

    /**
     * [View.setOnClickListener]
     */
    fun setOnClickListener(@IdRes viewId: Int, listener: View.OnClickListener) = getView<View>(viewId)
            .setOnClickListener(object : DebouncingOnClickListener() {
                override fun doClick(v: View) = listener.onClick(v)
            })

    /**
     * [View.setOnLongClickListener]
     */
    fun setOnLongClickListener(@IdRes viewId: Int, listener: View.OnLongClickListener) = getView<View>(viewId).setOnLongClickListener(listener)

    /**
     * Set the visibility state of this view.
     *
     * @param visibility One of [View.VISIBLE], [View.INVISIBLE], or [View.GONE].
     */
    fun setVisibility(@IdRes viewId: Int, visibility: Int) = getView<View>(viewId).apply {
        this.visibility = visibility
    }

    /**
     * Set the background to a given resource. The resource should refer to
     * a Drawable object or 0 to remove the background.
     *
     * @param resId The identifier of the resource.
     */
    fun setBackgroundResource(@IdRes viewId: Int, @DrawableRes resId: Int) = getView<View>(viewId).setBackgroundResource(resId)

    /**
     * [TextView.setGravity] (int)}
     */
    fun setTextGravity(@IdRes viewId: Int, gravity: Int) = getView<TextView>(viewId).apply {
        this.gravity = gravity
    }

    /**
     * 设置TextViewImage，方向个数必须和res个数相同
     *
     * @param viewId  View ID
     * @param gravity 多个方向可组合使用 [Gravity.START]|[Gravity.TOP]|[Gravity.END]|[Gravity.BOTTOM]
     * @param resId   资源ID
     */
    fun setTextImage(@IdRes viewId: Int, gravity: Int, @DrawableRes vararg resId: Int) {
        if (resId.isEmpty())
            return

        // 初始化Drawable
        var length: Int = resId.size
        val initDrawables: Array<Drawable?> = arrayOfNulls(length)
        for (i: Int in 0 until length) {
            val drawable: Drawable? = ContextCompat.getDrawable(getContext(), resId[i])
            drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            initDrawables[i] = drawable
        }

        // 设置Drawable
        val setDrawables: Array<Drawable?> = arrayOfNulls(4)
        try {
            if (gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
                setDrawables[3] = initDrawables[--length]
            }
            if (gravity and Gravity.END == Gravity.END) {
                setDrawables[2] = initDrawables[--length]
            }
            if (gravity and Gravity.TOP == Gravity.TOP) {
                setDrawables[1] = initDrawables[--length]
            }
            if (gravity and Gravity.START == Gravity.START) {
                setDrawables[0] = initDrawables[--length]
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("方向设置个数必须与资源id个数一致.")
        }

        getView<TextView>(viewId).apply {
            setCompoundDrawables(setDrawables[0], setDrawables[1], setDrawables[2], setDrawables[3])
        }
    }

    /**
     * [TextView.setText]
     */
    fun setText(@IdRes viewId: Int, @StringRes resId: Int) = getView<TextView>(viewId).setText(resId)

    /**
     * [TextView.setText]
     */
    fun setText(@IdRes viewId: Int, text: CharSequence?) = getView<TextView>(viewId).apply {
        this.text = text
    }

    /**
     * [TextView.setTextColor]
     */
    fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int) = getView<TextView>(viewId).setTextColor(color)

    /**
     * [TextView.setTextColor] and [ContextCompat.getColor]
     */
    fun setTextColorRes(@IdRes viewId: Int, @ColorRes resId: Int) = getView<TextView>(viewId).setTextColor(ContextCompat.getColor(getContext(), resId))

    /**
     * [ImageView.setImageResource]
     */
    fun setImageResource(@IdRes viewId: Int, @DrawableRes resId: Int) = getView<ImageView>(viewId).setImageResource(resId)

    /**
     * [ImageView.setImageDrawable]
     */
    fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable?) = getView<ImageView>(viewId).setImageDrawable(drawable)

    /**
     * [ImageView.setImageDrawable]
     */
    fun setImageColor(@IdRes viewId: Int, @ColorRes resId: Int) = getView<ImageView>(viewId).setImageDrawable(ColorDrawable(ContextCompat.getColor(getContext(), resId)))

    /* 可自行扩展View及其子类的方法... */
}