package cn.wukang.kotlinrvadapter.ui

import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import cn.wukang.kotlinrvadapter.R
import cn.wukang.kotlinrvadapter.manager.CountryManager
import cn.wukang.kotlinrvadapter.model.Country
import cn.wukang.library.adapter.base.BaseViewHolder
import cn.wukang.library.adapter.multi.MultiAdapter

/**
 * Multi Fragment
 *
 * @author wukang
 */
class MainMultiFragment : MainFragment() {
    private lateinit var header: Country
    private lateinit var adapter: MultiAdapter<Country>

    override fun getLayoutId(): Int = R.layout.fragment_main_rv

    override fun initView() {
        header = Country()
        header.setCountryNameEn("Recycler View Multi Adapter Item.")

        val list: List<Country> = listOf(header) + CountryManager.getInstance().getCountryList()
        adapter = object : MultiAdapter<Country>(list) {
            override fun getItemLayoutId(position: Int, t: Country?): Int {
                return if (position == 0) R.layout.rv_multi_title else R.layout.rv_multi_content
            }

            override fun convert(position: Int, t: Country?, holder: BaseViewHolder, @LayoutRes layoutId: Int) {
                if (t != null) {
                    when (layoutId) {
                        R.layout.rv_multi_title -> holder.setText(R.id.multi_title_ab, t.getCountryNameEn())
                        R.layout.rv_multi_content -> {
                            holder.setText(R.id.multi_content_id, t.getCountryId().toString(10))
                            holder.setText(R.id.multi_content_name, t.getCountryNameCn())
                            holder.setText(R.id.multi_content_code, "+" + t.getCountryCode())
                        }
                    }
                }
            }

            override fun onItemClick(v: View, position: Int, t: Country?, @LayoutRes layoutId: Int) {
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
        commonRv?.layoutManager = LinearLayoutManager(context)
        commonRv?.adapter = adapter
    }

    override fun asc() {
        // 正序
        val data: List<Country> = CountryManager.getInstance().getCountryList()
        val list: List<Country> = listOf(header) + data.sortedBy { it.getCountryId() }
        // 刷新
        adapter.setData(list)
        adapter.notifyDataSetChanged()
    }

    override fun desc() {
        // 倒序
        val data: List<Country> = CountryManager.getInstance().getCountryList()
        val list: List<Country> = listOf(header) + data.sortedByDescending { it.getCountryId() }
        // 刷新
        adapter.setData(list)
        adapter.notifyDataSetChanged()
    }

    override fun shuffle() {
        // 乱序
        val data: List<Country> = CountryManager.getInstance().getCountryList()
        val list: List<Country> = listOf(header) + data.shuffled()
        // 刷新
        adapter.setData(list)
        adapter.notifyDataSetChanged()
    }

}