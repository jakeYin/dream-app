package cn.sddman.download.listener

import android.graphics.Bitmap

interface GetThumbnailsListener {
    fun success(bitmap: Bitmap?)
}
