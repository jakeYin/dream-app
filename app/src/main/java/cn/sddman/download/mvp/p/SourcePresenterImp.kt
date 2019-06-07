package cn.sddman.download.mvp.p

import android.os.AsyncTask
import cn.sddman.download.mvp.e.MagnetInfo
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.mvp.m.DownLoadModel
import cn.sddman.download.mvp.m.DownLoadModelImp
import cn.sddman.download.mvp.m.TaskModel
import cn.sddman.download.mvp.m.TaskModelImp
import cn.sddman.download.mvp.v.SourceView

class SourcePresenterImp(private val sourceView: SourceView) : SourcePresenter {
    private val taskModel: TaskModel
    private val downLoadModel: DownLoadModel

    init {
        taskModel = TaskModelImp()
        downLoadModel = DownLoadModelImp()
    }

    override fun searchMagnet(rule: MagnetRule, keyword: String, page: Int) {

    }

    class IAsyncTask : AsyncTask<MagnetRule, Object, MagnetInfo>() {
        override fun doInBackground(vararg params: MagnetRule?): MagnetInfo {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}
