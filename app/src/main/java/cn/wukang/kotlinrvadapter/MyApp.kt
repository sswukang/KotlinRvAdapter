package cn.wukang.kotlinrvadapter

import android.app.Application
import cn.wukang.kotlinrvadapter.manager.CountryManager

/**
 * My Application
 *
 * @author wukang
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CountryManager.getInstance().init(this)
    }
}