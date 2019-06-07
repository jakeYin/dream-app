package cn.sddman.download.mvp.e

import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

@Table(name = "TorrentTask")
class TorrentTaskEntity {
    @Column(name = "id", isId = true, autoGen = true)
    private val id: Int = 0
    @Column(name = "taskId")
    private val downTaskId: Int = 0
    @Column(name = "taskId")
    private val baseFolder: String? = null
    @Column(name = "taskId")
    private val subPath: String? = null
    @Column(name = "taskId")
    private val fileSize: Long = 0
    @Column(name = "taskId")
    private val fileName: String? = null
    @Column(name = "taskId")
    private val fileIndex: Int = 0
}
