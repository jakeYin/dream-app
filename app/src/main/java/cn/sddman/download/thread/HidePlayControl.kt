package cn.sddman.download.thread

import android.os.Handler
import android.os.Message
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.v.PlayerView

class HidePlayControl(private val playerView: PlayerView) {
    private lateinit var mHideHandler: HideHandler

    private val hideRunable = Runnable { mHideHandler.obtainMessage(Const.MSG_HIDE).sendToTarget() }

    init {
        mHideHandler = HideHandler()
    }

    inner class HideHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                Const.MSG_HIDE -> playerView.controlViewHide()
            }

        }
    }

    fun startHideTimer() {//开始计时,三秒后执行runable
        mHideHandler.removeCallbacks(hideRunable)
        playerView.controlViewToggle()
        mHideHandler.postDelayed(hideRunable, 3000)
    }

    fun endHideTimer() {//移除runable,将不再计时
        mHideHandler.removeCallbacks(hideRunable)
    }

    fun resetHideTimer() {//重置计时
        mHideHandler.removeCallbacks(hideRunable)
        mHideHandler.postDelayed(hideRunable, 3000)
    }

}
