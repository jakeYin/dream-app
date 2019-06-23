package cn.sddman.download.util

import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.DownloadTaskEntity
import org.xutils.DbManager
import org.xutils.ex.DbException
import org.xutils.x
import java.io.File

class DBTools private constructor() {
    private val daoConfig: DbManager.DaoConfig

    init {
        FileTools.mkdirs(Const.DB_SDCARD_PATH)
        daoConfig = DbManager.DaoConfig()
                .setDbName(Const.DB_NAME)
                .setDbDir(File(Const.DB_SDCARD_PATH))
                .setDbVersion(6)
                .setDbOpenListener { db ->
                    // 开启WAL, 对写入加速提升巨大
                    db.database.enableWriteAheadLogging()
                }.setDbUpgradeListener { db, oldVersion, newVersion ->
                    try {
                        db.dropTable(DownloadTaskEntity::class.java)
                    } catch (e: DbException) {
                        e.printStackTrace()
                    }
                }
    }

    fun db(): DbManager {
        return x.getDb(daoConfig)
    }

    companion object {
        private var dbTools: DBTools? = null

        val instance: DBTools
            @Synchronized get() {
                if (dbTools == null) {
                    dbTools = DBTools()
                }
                return dbTools!!
            }
    }
}
