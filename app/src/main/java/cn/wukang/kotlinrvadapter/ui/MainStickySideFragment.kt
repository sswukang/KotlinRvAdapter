package cn.wukang.kotlinrvadapter.ui

import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.view.View
import cn.wukang.kotlinrvadapter.R
import cn.wukang.kotlinrvadapter.manager.CountryManager
import cn.wukang.kotlinrvadapter.model.Country
import cn.wukang.library.adapter.base.BaseViewHolder
import cn.wukang.library.adapter.sticky.StickyHeaderAdapter
import cn.wukang.library.lib.side.SideAndStickyHeaderRecyclerView
import cn.wukang.library.lib.side.SideBar

/**
 * Sticky Side Fragment
 *
 * @author wukang
 */
class MainStickySideFragment : MainFragment() {
    private lateinit var adapter: StickyHeaderAdapter<Country>
    private var rvSideSticky: SideAndStickyHeaderRecyclerView? = null

    override fun getLayoutId(): Int = R.layout.fragment_main_rv_side_sticky

    override fun initView() {
        adapter = object : StickyHeaderAdapter<Country>(R.layout.rv_sticky_title, R.layout.rv_sticky_content, listOf()) {
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

        rvSideSticky = findViewById(R.id.rv_side_sticky)
        with(rvSideSticky) {
            this?.addItemDecoration(DividerItemDecoration(getCreatorActivity(), DividerItemDecoration.VERTICAL))
            this?.setStickyHeaderAdapter(this@MainStickySideFragment.adapter)
            return@with this?.linkageMove(true)
        }

        // 初始化排序一次
        asc()
    }

    override fun asc() = CountryManager.getInitialsMap().sideStickySort { it.keys.sorted() }

    override fun desc() = CountryManager.getInitialsMap().sideStickySort { it.keys.sortedDescending() }

    override fun shuffle() = CountryManager.getInitialsMap().sideStickySort { it.keys.shuffled() }

    // Side Sticky 排序的通用方法
    private fun Map<String, List<Country>>.sideStickySort(block: (Map<String, List<Country>>) -> List<String>) = sideStickyOtherSet(block(this), this)

    // Side Sticky 排序的其他设置
    private fun sideStickyOtherSet(keys: List<String>, initials: Map<String, List<Country>>) {
        var data: List<Country> = listOf()
        keys.forEach { data += initials.getOrElse(it) { listOf() }.sortedBy { it.countryNameEn } }
        // rv设置
        with(rvSideSticky) {
            this?.setIndexItems(keys)
            return@with this?.setOnSelectIndexItemListener(object : SideBar.OnSelectIndexItemListener {
                override fun onSelectIndexItem(index: String) {
                    val position: Int = data.indexOfFirst { index == it.countryNameEn?.take(1) }
                    if (0 <= position && position < data.size) {
                        this@with.moveToPosition(position)
                    }
                }
            })
        }
        // 刷新
        adapter.apply {
            setData(data)
            notifyDataSetChanged()
        }
    }
}