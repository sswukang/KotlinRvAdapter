package cn.wukang.kotlinrvadapter.base

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * FragmentPager 通用适配器
 *
 * @author wukang
 */
class BaseFragmentAdapter<out A : BaseActivity, F : BaseFragment<A>>
(fm: FragmentManager, private var fragList: List<F>, private var fragTags: List<CharSequence>?) : FragmentPagerAdapter(fm) {
    constructor(fm: FragmentManager, fragList: List<F>) : this(fm, fragList, null)

    override fun getItem(position: Int): F = fragList[position]

    override fun getCount(): Int = fragList.size

    override fun getPageTitle(position: Int): CharSequence? = fragTags?.get(position)
}