package cn.wukang.kotlinrvadapter.ui

import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import cn.wukang.kotlinrvadapter.R
import cn.wukang.kotlinrvadapter.manager.CountryManager
import cn.wukang.kotlinrvadapter.model.Country
import cn.wukang.library.adapter.base.BaseViewHolder
import cn.wukang.library.adapter.single.SingleAdapter

/**
 * Single Fragment
 *
 * @author wukang
 */
class MainSingleFragment : MainFragment() {
    private lateinit var adapter: SingleAdapter<Country>

    override fun getLayoutId(): Int = R.layout.fragment_main_rv

    override fun initView() {
        adapter = object : SingleAdapter<Country>(R.layout.rv_single_item, CountryManager.getCountryList()) {
            override fun convert(position: Int, t: Country?, holder: BaseViewHolder) {
                if (t != null) {
                    holder.setText(R.id.single_item_name, t.countryNameCn)
                    holder.setText(R.id.single_item_code, "+" + t.countryCode)
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
            this?.layoutManager = LinearLayoutManager(context)
            this?.adapter = this@MainSingleFragment.adapter
        }
    }

    override fun asc() {
        // 正序
        val data: List<Country> = CountryManager.getCountryList().sortedBy { it.countryCode }
        // 刷新
        adapter.apply {
            setData(data)
            notifyDataSetChanged()
        }
    }

    override fun desc() {
        // 倒序
        val data: List<Country> = CountryManager.getCountryList().sortedByDescending { it.countryCode }
        // 刷新
        adapter.setData(data)
        adapter.apply {
            setData(data)
            notifyDataSetChanged()
        }
    }

    override fun shuffle() {
        // 乱序
        val data: List<Country> = CountryManager.getCountryList().shuffled()
        // 刷新
        adapter.apply {
            setData(data)
            notifyDataSetChanged()
        }
    }
}