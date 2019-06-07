package cn.sddman.download.mvp.v

interface PlayerView {
    fun initPlayer()
    fun openVideo(path: String)
    fun playPause()
    fun setTimeTextView(currentPlayTimeMs: Int, durationTimeMs: Int)
    fun updateUIPlayStation(currentPlayTimeMs: Int, durationTimeMs: Int)
    fun ismIsNeedUpdateUIProgress(): Boolean
    fun ismIsTouchingSeekbar(): Boolean
    fun controlViewToggle()
    fun controlViewShow()
    fun controlViewHide()
    fun setVideoTile(name: String)
    fun userSeekPlayProgress(currentPlayTimeMs: Int)
}
