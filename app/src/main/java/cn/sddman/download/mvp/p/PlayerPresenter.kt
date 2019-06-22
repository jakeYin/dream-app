package cn.sddman.download.mvp.p

interface PlayerPresenter {
    fun setHistoryCurrentPlayTimeMs()
    fun updatePlayerTime(currentPlayTimeMs: Int, durationTimeMs: Int)
}
