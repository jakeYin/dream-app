package com.dream.tlj.thread

import android.os.Handler
import android.os.Message

import com.dream.tlj.common.Const
import com.dream.tlj.mvp.v.PlayerView

class UpdataUIPlayHandler(private val playerView: PlayerView) : Handler() {
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            Const.UI_UPDATE_PLAY_STATION -> playerView.updateUIPlayStation(msg.arg1, msg.arg2)
            else -> {
            }
        }
    }
}
