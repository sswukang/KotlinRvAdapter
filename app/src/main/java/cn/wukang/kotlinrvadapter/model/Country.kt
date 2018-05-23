package cn.wukang.kotlinrvadapter.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * 国家解析类
 *
 * @author wukang
 */
class Country : Parcelable {
    // 国家id
    @SerializedName("country_id")
    private var countryId: Int = 0
    // 国家编码
    @SerializedName("country_code")
    private var countryCode: Int = 0
    // 国家英文名
    @SerializedName("country_name_en")
    private var countryNameEn: String? = null
    // 国家中文名
    @SerializedName("country_name_cn")
    private var countryNameCn: String? = null
    // 国家英文缩写
    private var ab: String? = null

    fun getCountryId(): Int {
        return countryId
    }

    fun setCountryId(countryId: Int) {
        this.countryId = countryId
    }

    fun getCountryCode(): Int {
        return countryCode
    }

    fun setCountryCode(countryCode: Int) {
        this.countryCode = countryCode
    }

    fun getCountryNameEn(): String? {
        return countryNameEn
    }

    fun setCountryNameEn(countryNameEn: String) {
        this.countryNameEn = countryNameEn
    }

    fun getCountryNameCn(): String? {
        return countryNameCn
    }

    fun setCountryNameCn(countryNameCn: String) {
        this.countryNameCn = countryNameCn
    }

    fun getAb(): String? {
        return ab
    }

    fun setAb(ab: String) {
        this.ab = ab
    }

    override fun toString(): String = "Country(countryId=$countryId, countryCode=$countryCode, countryNameEn=$countryNameEn, countryNameCn=$countryNameCn, ab=$ab)"

    constructor()

    constructor(`in`: Parcel) {
        this.countryId = `in`.readInt()
        this.countryCode = `in`.readInt()
        this.countryNameEn = `in`.readString()
        this.countryNameCn = `in`.readString()
        this.ab = `in`.readString()
    }

    companion object {
        val CREATOR: Parcelable.Creator<Country> = object : Parcelable.Creator<Country> {
            override fun createFromParcel(source: Parcel): Country {
                return Country(source)
            }

            override fun newArray(size: Int): Array<Country> {
                return Array(size, { Country() })
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.countryId)
        dest.writeInt(this.countryCode)
        dest.writeString(this.countryNameEn)
        dest.writeString(this.countryNameCn)
        dest.writeString(this.ab)
    }
}