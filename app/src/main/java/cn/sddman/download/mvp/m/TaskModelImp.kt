package cn.sddman.download.mvp.m

import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.util.DBTools
import org.xutils.ex.DbException

class TaskModelImp : TaskModel {
    override fun findAllTask(): MutableList<DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(DownloadTaskEntity::class.java).findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findTaskById(id: Int): DownloadTaskEntity? {
        try {
            return DBTools.instance.db().findById(DownloadTaskEntity::class.java, id)
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findTaskByUrl(url: String): MutableList<DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(DownloadTaskEntity::class.java).where("url", "=", url).findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findTaskByHash(hash: String): MutableList<DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(DownloadTaskEntity::class.java).where("hash", "=", hash).findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findLoadingTask(): MutableList<DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(DownloadTaskEntity::class.java)
                    .where("mTaskStatus", "<>", Const.DOWNLOAD_SUCCESS)
                    .orderBy("createDate", true)
                    .findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findDowningTask(): MutableList<DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(DownloadTaskEntity::class.java)
                    .where("mTaskStatus", "in", intArrayOf(Const.DOWNLOAD_LOADING, Const.DOWNLOAD_CONNECTION))
                    .and("taskId", "<>", 0)
                    // .and("mTaskStatus", "=", Const.DOWNLOAD_FAIL)
                    .findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findSuccessTask(): MutableList<DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(DownloadTaskEntity::class.java)
                    .where("mTaskStatus", "=", Const.DOWNLOAD_SUCCESS)
                    .orderBy("createDate", true)
                    .findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun updateTask(task: DownloadTaskEntity): DownloadTaskEntity {
        try {
            DBTools.instance.db().saveOrUpdate(task)
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return task
    }

    override fun deleTask(task: DownloadTaskEntity): Boolean {
        try {
            DBTools.instance.db().delete(task)
            return true
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return false
    }
}
