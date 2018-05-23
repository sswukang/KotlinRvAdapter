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
                CountryManager.getInstance().getCountryList()) {
            override fun setHeaderHeight(): Int {
                return resources.getDimensionPixelSize(R.dimen.main_sticky_header_height)
            }

            override fun getHeaderId(position: Int, t: Country?): Long {
                return t?.getCountryNameEn()?.get(0)?.toLong() ?: getItemId(position)
            }

            override fun convertHeader(position: Int, t: Country?, holder: BaseViewHolder) {
                if (t != null) {
                    holder.setText(R.id.sticky_title_initials, t.getCountryNameEn()?.substring(0, 1))
                }
            }

            override fun convert(position: Int, t: Country?, holder: BaseViewHolder) {
                if (t != null) {
                    holder.setText(R.id.sticky_content_name, t.getCountryNameCn() + "(" + t.getCountryNameEn() + ")")
                    holder.setText(R.id.sticky_content_code, "+" + t.getCountryCode())
                }
            }

            override fun onItemClick(v: View, position: Int, t: Country?) {
                if (t != null) {
                    Snackbar.make(v, t.toString(), Snackbar.LENGTH_LONG)
                            .addCallback(object : Snackbar.Callback() {
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                    setToolbarContent(t.getCountryNameCn(), t.getCountryNameEn())
                                }
                            }).show()
                }
            }
        }

        val commonRv: RecyclerView? = findViewById(R.id.common_rv)
        commonRv?.layoutManager = LinearLayoutManager(getCreatorActivity())
        commonRv?.addItemDecoration(DividerItemDecoration(getCreatorActivity(), DividerItemDecoration.VERTICAL))
        commonRv?.addItemDecoration(StickyRecyclerHeadersDecoration(adapter)) // 必须添加
        commonRv?.adapter = adapter
    }

    override fun asc() {
        // 正序
        val data: List<Country> = CountryManager.getInstance().getCountryList().sortedBy { it.getCountryNameEn() }
        // 刷新
        adapter.setData(data)
        adapter.notifyDataSetChanged()
    }

    override fun desc() {
        // 倒序
        val data: List<Country> = CountryManager.getInstance().getCountryList().sortedByDescending { it.getCountryNameEn() }
        // 刷新
        adapter.setData(data)
        adapter.notifyDataSetChanged()
    }

    override fun shuffle() {
        // 首字母map集合
        val initials: Map<String, List<Country>> = CountryManager.getInstance().getInitialsMap()
        // 组成首字母乱序集合
        var data: List<Country> = listOf()
        initials.keys.shuffled().forEach { data += initials.getOrElse(it, { listOf() }).sortedBy { it.getCountryNameEn() } }
        // 刷新
        adapter.setData(data)
        adapter.notifyDataSetChanged()
    }
 }