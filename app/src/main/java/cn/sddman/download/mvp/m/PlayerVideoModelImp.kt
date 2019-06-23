package cn.sddman.download.mvp.m

import cn.sddman.download.mvp.e.PlayerVideoEntity
import cn.sddman.download.util.DBTools
import org.xutils.ex.DbException

class PlayerVideoModelImp : PlayerVideoModel {
    override fun findAllVideo(): List<PlayerVideoEntity>? {
        try {
            return DBTools.instance.db().selector(PlayerVideoEntity::class.java).findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findVideoByPath(path: String): List<PlayerVideoEntity>? {
        try {
            return DBTools.instance.db().selector(PlayerVideoEntity::class.java).where("localPath", "=", path).findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findVideoById(id: Int): PlayerVideoEntity? {
        try {
            return DBTools.instance.db().findById(PlayerVideoEntity::class.java, id)
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun saveOrUpdata(video: PlayerVideoEntity): PlayerVideoEntity {
        try {
            DBTools.instance.db().saveOrUpdate(video)
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return video
    }


    override fun updateVideo(video: PlayerVideoEntity): PlayerVideoEntity {
        try {
            DBTools.instance.db().saveOrUpdate(video)
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return video
    }

    override fun deleVideo(video: PlayerVideoEntity): Boolean {
        try {
            DBTools.instance.db().delete(video)
            return true
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return false
    }
}
