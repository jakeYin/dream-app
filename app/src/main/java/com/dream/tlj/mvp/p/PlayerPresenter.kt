package com.dream.tlj.mvp.p

interface PlayerPresenter {
    fun setHistoryCurrentPlayTimeMs()
    fun updatePlayerTime(currentPlayTimeMs: Int, durationTimeMs: Int)
}
