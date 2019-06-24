package com.dream.tlj.mvp.e

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MagnetSearchBean(
        var rule: com.dream.tlj.mvp.e.MagnetRule,
        var keyword: String,
        var page: Int,
        var detailUrl:String
): Parcelable{
        constructor(rule: com.dream.tlj.mvp.e.MagnetRule, keyword:String, page:Int) : this(rule,keyword,page,"")
        constructor(rule: com.dream.tlj.mvp.e.MagnetRule, detailUrl:String) : this(rule,"",0,detailUrl)
}
