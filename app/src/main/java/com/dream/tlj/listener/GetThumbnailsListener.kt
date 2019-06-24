package com.dream.tlj.listener

import android.graphics.Bitmap

interface GetThumbnailsListener {
    fun success(bitmap: Bitmap?)
}
