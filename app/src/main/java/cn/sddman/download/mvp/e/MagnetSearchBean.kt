package cn.sddman.download.mvp.e

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MagnetSearchBean(
        var rule: MagnetRule,
        var keyword: String,
        var page: Int
        ): Parcelable