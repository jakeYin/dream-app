package cn.sddman.download.thread

import android.os.Handler
import android.os.Message

import com.aplayer.aplayerandroid.APlayerAndroid

import cn.sddman.download.common.Const
import cn.sddman.download.mvp.v.PlayerView

class UpdatePlayUIProcess(private val playerView: PlayerView, private val handler: Handler, private val aPlayer: APlayerAndroid?) : Runnable {
    override fun run() {
        while (playerView.ismIsNeedUpdateUIProgress()) {
            if (!playerView.ismIsTouchingSeekbar()) {
                var currentPlayTime = 0
                var durationTime = 0
                if (null != aPlayer) {
                    currentPlayTime = aPlayer.position
                    durationTime = aPlayer.duration
                }
                val msg = handler.obtainMessage(Const.UI_UPDATE_PLAY_STATION, currentPlayTime, durationTime)
                handler.sendMessage(msg)
            }
            try {
                Thread.sleep(Const.TIMER_UPDATE_INTERVAL_TIME.toLong())
            } catch (e: InterruptedException) {
            }

        }
    }
}
