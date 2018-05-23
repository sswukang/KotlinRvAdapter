package cn.wukang.kotlinrvadapter.base

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Activity基类
 *
 * @author wukang
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * @return 设置视图id
     */
    abstract fun getLayoutId(): Int

    /**
     * 初始化视图
     */
    abstract fun initView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initView()
    }

    fun getContext(): Context = this

    fun getActivity(): BaseActivity = this
}