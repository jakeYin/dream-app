package cn.sddman.download.mvp.e

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MagnetRule(
        var id: String,
        var site: String,
        var url: String,
        var parserClass: String,
        var parserDetailClass: String?,
        var list: String,
        var href: String,
        var name: String,
        var desc: String,
        var detailUrl: String,
        var detailBaseUrl: String,
        var detailLinks:String,
        var source: String): Parcelable
