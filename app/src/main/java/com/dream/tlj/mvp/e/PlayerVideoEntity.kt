package com.dream.tlj.mvp.e

import android.graphics.Bitmap

import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

@Table(name = "PlayerVideo")
class PlayerVideoEntity {
    @Column(name = "id", isId = true, autoGen = true)
    var id: Int = 0
    @Column(name = "name")
    var name: String? = null
    @Column(name = "currentPlayTimeMs")
    var currentPlayTimeMs: Int = 0
    @Column(name = "durationTimeMs")
    var durationTimeMs: Int = 0
    @Column(name = "localPath")
    var localPath: String? = null
    var thumbnail: Bitmap? = null
}
