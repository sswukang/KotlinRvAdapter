package cn.wukang.kotlinrvadapter.util

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 工具类
 *
 * @author wukang
 */
class Utils {
    companion object {
        /**
         * 从assets读取文本信息
         */
        @Throws(IOException::class)
        fun getTextFromAssets(context: Context, fileName: String): String {
            val inputReader = InputStreamReader(context.assets.open(fileName))
            val bufReader = BufferedReader(inputReader)
            val result = StringBuilder()
            while (true) {
                result.append(bufReader.readLine() ?: break)
            }
            return result.toString()
        }
    }
}