package cn.wukang.kotlinrvadapter.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.view.menu.MenuAdapter
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.ListPopupWindow
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import cn.wukang.kotlinrvadapter.R
import cn.wukang.kotlinrvadapter.base.BaseActivity
import cn.wukang.kotlinrvadapter.base.BaseFragmentAdapter
import cn.wukang.kotlinrvadapter.view.NoScrollViewPager

/**
 * 主界面
 *
 * @author wukang
 */
class MainActivity : BaseActivity() {
    private lateinit var topToolbar: Toolbar
    private lateinit var mainViewPager: NoScrollViewPager

    // 左pop
    private lateinit var leftMenuPop: ListPopupWindow
    // 右pop
    private lateinit var rightMenuPop: ListPopupWindow
    // FragmentAdapter
    private lateinit var fragmentAdapter: BaseFragmentAdapter<MainActivity, MainFragment>

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        topToolbar = findViewById(R.id.top_toolbar)
        mainViewPager = findViewById(R.id.main_view_pager)

        // 初始化ActionBar
        topToolbar.apply {
            setTitleTextColor(Color.WHITE)
            setSubtitleTextColor(Color.argb(Math.round(255 * 0.8f), 255, 255, 255))
            this@MainActivity.setSupportActionBar(this)
        }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_top_menu)
        }

        // 初始化PopupWindow
        initLeftMenuPop()
        initRightMenuPop()

        // 初始化ViewPager
        fragmentAdapter = BaseFragmentAdapter(supportFragmentManager, listOf(MainSingleFragment(),
                MainMultiFragment(), MainStickyFragment(), MainStickySideFragment()))
        mainViewPager.adapter = fragmentAdapter
    }

    @SuppressLint("RestrictedApi")
    private fun initLeftMenuPop() {
        val menuBuilder: MenuBuilder = MenuBuilder(getContext()).apply {
            setOptionalIconsVisible(true)
            add(R.string.main_single).setIcon(R.drawable.ic_main_single)
            add(R.string.main_multi).setIcon(R.drawable.ic_main_multi)
            add(R.string.main_sticky).setIcon(R.drawable.ic_main_sticky)
            add(R.string.main_sticky_side).setIcon(R.drawable.ic_main_sticky_side)
        }
        leftMenuPop = ListPopupWindow(getContext()).apply {
            width = resources.displayMetrics.widthPixels / 2
            height = ListPopupWindow.WRAP_CONTENT
            anchorView = topToolbar
            isModal = true
            setAdapter(MenuAdapter(menuBuilder, layoutInflater, true))
            setDropDownGravity(Gravity.START)
            setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
                mainViewPager.setCurrentItem(position, false)
                dismiss()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun initRightMenuPop() {
        val menuBuilder: MenuBuilder = MenuBuilder(getContext()).apply {
            setOptionalIconsVisible(true)
            add(R.string.main_asc).setIcon(R.drawable.ic_main_asc)
            add(R.string.main_desc).setIcon(R.drawable.ic_main_desc)
            add(R.string.main_shuffle).setIcon(R.drawable.ic_main_shuffle)
        }
        rightMenuPop = ListPopupWindow(getContext()).apply {
            width = resources.displayMetrics.widthPixels / 2
            height = ListPopupWindow.WRAP_CONTENT
            anchorView = topToolbar
            isModal = true
            setAdapter(MenuAdapter(menuBuilder, layoutInflater, true))
            setDropDownGravity(Gravity.END)
            setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
                when (position) {
                    0 -> fragmentAdapter.getItem(mainViewPager.currentItem).asc()
                    1 -> fragmentAdapter.getItem(mainViewPager.currentItem).desc()
                    2 -> fragmentAdapter.getItem(mainViewPager.currentItem).shuffle()
                }
                dismiss()
            }
        }
    }

    fun setTopToolbarText(title: String?, subtitle: String?) = topToolbar.apply {
        this.title = title
        this.subtitle = subtitle
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            leftMenuPop.show()
            true
        }
        R.id.main_pop -> {
            rightMenuPop.show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
