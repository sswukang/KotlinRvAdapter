package cn.wukang.kotlinrvadapter.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * 国家解析类
 *
 * @param countryId 国家id
 * @param countryCode 国家编码
 * @param countryNameEn 国家英文名
 * @param countryNameCn 国家中文名
 * @param ab 国家英文缩写
 * @author wukang
 */
data class Country(@SerializedName("country_id") var countryId: Int,
                   @SerializedName("country_code") var countryCode: Int,
                   @SerializedName("country_name_en") var countryNameEn: String?,
                   @SerializedName("country_name_cn") var countryNameCn: String?,
                   @SerializedName("ab") var ab: String?) : Parcelable {
    constructor() : this(0, 0, null, null, null)

    constructor(`in`: Parcel) : this(`in`.readInt(), `in`.readInt(), `in`.readString(), `in`.readString(), `in`.readString())

    companion object CREATOR : Parcelable.Creator<Country> {
        override fun createFromParcel(source: Parcel): Country = Country(source)

        override fun newArray(size: Int): Array<Country> = Array(size) { Country() }
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(countryId)
        writeInt(countryCode)
        writeString(countryNameEn)
        writeString(countryNameCn)
        writeString(ab)
    }
}