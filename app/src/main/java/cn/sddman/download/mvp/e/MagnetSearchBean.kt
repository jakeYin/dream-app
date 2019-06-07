package cn.sddman.download.mvp.e

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MagnetSearchBean(
        var rule: MagnetRule,
        var keyword: String,
        var page: Int,
        var detailUrl:String
): Parcelable{
        constructor(rule:MagnetRule,keyword:String,page:Int) : this(rule,keyword,page,"")
        constructor(rule:MagnetRule,detailUrl:String) : this(rule,"",0,detailUrl)
}
