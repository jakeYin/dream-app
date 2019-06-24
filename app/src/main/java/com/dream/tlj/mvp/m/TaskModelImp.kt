package com.dream.tlj.mvp.m

import com.dream.tlj.common.Const
import com.dream.tlj.mvp.e.DownloadTaskEntity
import com.dream.tlj.util.DBTools
import org.xutils.ex.DbException

class TaskModelImp : TaskModel {
    override fun findAllTask(): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(com.dream.tlj.mvp.e.DownloadTaskEntity::class.java).findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findTaskById(id: Int): com.dream.tlj.mvp.e.DownloadTaskEntity? {
        try {
            return DBTools.instance.db().findById(com.dream.tlj.mvp.e.DownloadTaskEntity::class.java, id)
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findTaskByUrl(url: String): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(com.dream.tlj.mvp.e.DownloadTaskEntity::class.java).where("url", "=", url).findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findTaskByHash(hash: String): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(com.dream.tlj.mvp.e.DownloadTaskEntity::class.java).where("hash", "=", hash).findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findLoadingTask(): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(com.dream.tlj.mvp.e.DownloadTaskEntity::class.java)
                    .where("mTaskStatus", "<>", Const.DOWNLOAD_SUCCESS)
                    .orderBy("createDate", true)
                    .findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findDowningTask(): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(com.dream.tlj.mvp.e.DownloadTaskEntity::class.java)
                    .where("mTaskStatus", "in", intArrayOf(Const.DOWNLOAD_LOADING, Const.DOWNLOAD_CONNECTION))
                    .and("taskId", "<>", 0)
                    // .and("mTaskStatus", "=", Const.DOWNLOAD_FAIL)
                    .findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun findSuccessTask(): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>? {
        try {
            return DBTools.instance.db().selector(com.dream.tlj.mvp.e.DownloadTaskEntity::class.java)
                    .where("mTaskStatus", "=", Const.DOWNLOAD_SUCCESS)
                    .orderBy("createDate", true)
                    .findAll()
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun updateTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity): com.dream.tlj.mvp.e.DownloadTaskEntity {
        try {
            DBTools.instance.db().saveOrUpdate(task)
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return task
    }

    override fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity): Boolean {
        try {
            DBTools.instance.db().delete(task)
            return true
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return false
    }
}
