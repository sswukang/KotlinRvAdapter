package cn.wukang.kotlinrvadapter.manager

import android.content.Context
import cn.wukang.kotlinrvadapter.model.Country
import cn.wukang.kotlinrvadapter.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * 城市列表管理类
 *
 * @author wukang
 */
object CountryManager {
    private lateinit var countryList: List<Country>

    /**
     * 初始化数据
     */
    fun init(context: Context) = try {
        val json: String = Utils.getTextFromAssets(context, "countrycode.json")
        countryList = Gson().fromJson<List<Country>>(json, object : TypeToken<List<Country>>() {}.type)
    } catch (ignore: IOException) {
    }

    /**
     * @return 国家列表
     */
    fun getCountryList(): List<Country> = countryList

    /**
     * 得到列表的所有首字母集合
     *
     * @return 首字母map集合
     */
    fun getInitialsMap(): Map<String, List<Country>> = countryList.groupBy {
        return@groupBy it.countryNameEn?.take(1) ?: ""
    }
}