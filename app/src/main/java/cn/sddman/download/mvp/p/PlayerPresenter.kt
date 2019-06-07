package cn.sddman.download.mvp.p

interface PlayerPresenter {
    fun setHistoryCurrentPlayTimeMs()
    fun uaDataPlayerTime(currentPlayTimeMs: Int, durationTimeMs: Int)
}
