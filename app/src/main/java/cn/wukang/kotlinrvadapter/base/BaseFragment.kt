package cn.wukang.kotlinrvadapter.base

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent

/**
 * Fragment基类
 *
 * @author wukang
 */
abstract class BaseFragment<out A : BaseActivity> : Fragment() {
    /**
     * @return 设置视图id
     */
    abstract fun getLayoutId(): Int

    /**
     * 初始化视图
     */
    abstract fun initView()

    private var root: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (root == null) {
            root = inflater.inflate(getLayoutId(), container, false)
        }
        val parent: ViewParent? = root?.parent
        if (parent != null) {
            (parent as ViewGroup).removeView(root)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun <T : View> findViewById(@IdRes id: Int): T? = root?.findViewById(id)

    @Suppress("UNCHECKED_CAST")
    fun getCreatorActivity(): A? {
        return if (activity != null)
            activity as A
        else
            null
    }
}