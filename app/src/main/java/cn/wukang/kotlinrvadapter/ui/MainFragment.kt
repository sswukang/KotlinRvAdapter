package cn.wukang.kotlinrvadapter.ui

import cn.wukang.kotlinrvadapter.base.BaseFragment

/**
 * 主界面 base fragment
 *
 * @author wukang
 */
abstract class MainFragment : BaseFragment<MainActivity>() {
    abstract fun asc()

    abstract fun desc()

    abstract fun shuffle()

    protected fun setToolbarContent(title: String?, subtitle: String?) = getCreatorActivity()?.setTopToolbarText(title, subtitle)
}