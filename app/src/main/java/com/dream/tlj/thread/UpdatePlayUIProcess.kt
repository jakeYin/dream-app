package com.dream.tlj.thread

import android.os.Handler
import com.dream.tlj.common.Const
import com.dream.tlj.mvp.v.PlayerView
import com.aplayer.aplayerandroid.APlayerAndroid

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
