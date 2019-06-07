package cn.sddman.download.mvp.p

import com.aplayer.aplayerandroid.APlayerAndroid

import java.io.File

import cn.sddman.download.mvp.e.PlayerVideoEntity
import cn.sddman.download.mvp.m.PlayerVideoModel
import cn.sddman.download.mvp.m.PlayerVideoModelImp
import cn.sddman.download.mvp.v.PlayerView
import cn.sddman.download.util.FileTools

class PlayerPresenterImp(private val playerView: PlayerView, private val videoPath: String) : PlayerPresenter {
    private val playerVideoModel: PlayerVideoModel
    private var video: PlayerVideoEntity? = null

    init {
        playerVideoModel = PlayerVideoModelImp()
        val videos = playerVideoModel.findVideoByPath(videoPath)
        if (videos != null && videos.size > 0) {
            video = videos[0]
        } else {
            video = PlayerVideoEntity()
            video!!.localPath = videoPath
            video!!.name = FileTools.getFileName(videoPath)
            video = playerVideoModel.saveOrUpdata(video!!)
        }
        playerView.initPlayer()
        playerView.openVideo(video!!.localPath!!)
        playerView.setVideoTile(video!!.name!!)
    }

    override fun setHistoryCurrentPlayTimeMs() {
        if (video!!.currentPlayTimeMs > 0) {
            playerView.userSeekPlayProgress(video!!.currentPlayTimeMs)
        }
    }

    override fun uaDataPlayerTime(currentPlayTimeMs: Int, durationTimeMs: Int) {
        if (currentPlayTimeMs > 0 && durationTimeMs > 0) {
            video!!.currentPlayTimeMs = currentPlayTimeMs
            video!!.durationTimeMs = durationTimeMs
            video = playerVideoModel.saveOrUpdata(video!!)
        }
    }
}
