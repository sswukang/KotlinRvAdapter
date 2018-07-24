package cn.wukang.kotlinrvadapter.ui

import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import cn.wukang.kotlinrvadapter.R
import cn.wukang.kotlinrvadapter.manager.CountryManager
import cn.wukang.kotlinrvadapter.model.Country
import cn.wukang.library.adapter.base.BaseViewHolder
import cn.wukang.library.adapter.sticky.StickyHeaderAdapter
import cn.wukang.library.lib.stickyHeader.sticky.StickyRecyclerHeadersDecoration

/**
 * Sticky Fragment
 *
 * @author wukang
 */
class MainStickyFragment : MainFragment() {
    private lateinit var adapter: StickyHeaderAdapter<Country>

    override fun getLayoutId(): Int = R.layout.fragment_main_rv

    override fun initView() {
        adapter = object : StickyHeaderAdapter<Country>(R.layout.rv_sticky_title, R.layout.rv_sticky_content,
                CountryManager.getCountryList()) {
            override val headerHeight: Int = resources.getDimensionPixelSize(R.dimen.main_sticky_header_height)

            override fun getHeaderId(position: Int, t: Country?): Long = t?.countryNameEn?.get(0)?.toLong()
                    ?: getItemId(position)

            override fun convertHeader(position: Int, t: Country?, holder: BaseViewHolder) {
                if (t != null) {
                    holder.setText(R.id.sticky_title_initials, t.countryNameEn?.substring(0, 1))
                }
            }

            override fun convert(position: Int, t: Country?, holder: BaseViewHolder) {
                if (t != null) {
                    holder.setText(R.id.sticky_content_name, t.countryNameCn + "(" + t.countryNameEn + ")")
                    holder.setText(R.id.sticky_content_code, "+" + t.countryCode)
                }
            }

            override fun onItemClick(v: View, position: Int, t: Country?) {
                if (t != null) {
                    Snackbar.make(v, t.toString(), Snackbar.LENGTH_LONG)
                            .addCallback(object : Snackbar.Callback() {
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                    setToolbarContent(t.countryNameCn, t.countryNameEn)
                                }
                            }).show()
                }
            }
        }

        val commonRv: RecyclerView? = findViewById(R.id.common_rv)
        with(commonRv) {
            this?.layoutManager = LinearLayoutManager(getCreatorActivity())
            this?.addItemDecoration(DividerItemDecoration(getCreatorActivity(), DividerItemDecoration.VERTICAL))
            this?.addItemDecoration(StickyRecyclerHeadersDecoration(this@MainStickyFragment.adapter)) // 必须添加
            this?.adapter = this@MainStickyFragment.adapter
        }

        // 初始化排序一次
        asc()
    }

    override fun asc() = CountryManager.getInitialsMap().stickySort { it.keys.sorted() }

    override fun desc() = CountryManager.getInitialsMap().stickySort { it.keys.sortedDescending() }

    override fun shuffle() = CountryManager.getInitialsMap().stickySort { it.keys.shuffled() }

    // Sticky 排序的通用方法
    private fun Map<String, List<Country>>.stickySort(block: (Map<String, List<Country>>) -> List<String>) {
        // 创建一个空list
        var data: List<Country> = listOf()
        // 根据首字母的顺序，添加对应的数据列表
        block(this).forEach { data += this.getOrElse(it) { listOf() }.sortedBy { it.countryNameEn } }
        // 刷新
        adapter.apply {
            setData(data)
            notifyDataSetChanged()
        }
    }
}