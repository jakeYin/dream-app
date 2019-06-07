package cn.sddman.download.thread

import android.os.Handler
import android.os.Message

import cn.sddman.download.common.Const
import cn.sddman.download.mvp.v.PlayerView

class UpdataUIPlayHandler(private val playerView: PlayerView) : Handler() {
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            Const.UI_UPDATE_PLAY_STATION -> playerView.updateUIPlayStation(msg.arg1, msg.arg2)
            else -> {
            }
        }
    }
}
