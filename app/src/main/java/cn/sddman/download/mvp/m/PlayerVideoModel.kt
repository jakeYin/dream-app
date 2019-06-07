package cn.sddman.download.mvp.m

import cn.sddman.download.mvp.e.PlayerVideoEntity

interface PlayerVideoModel {
    fun findAllVideo(): List<PlayerVideoEntity>?
    fun findVideoByPath(path: String): List<PlayerVideoEntity>?
    fun findVideoById(id: Int): PlayerVideoEntity?
    fun saveOrUpdata(video: PlayerVideoEntity): PlayerVideoEntity?
    fun updateVideo(video: PlayerVideoEntity): PlayerVideoEntity?
    fun deleVideo(video: PlayerVideoEntity): Boolean
}
