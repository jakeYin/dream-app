package cn.sddman.download.mvp.p

import android.os.AsyncTask
import cn.sddman.download.spider.MagnetFetchInf
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.MagnetInfo
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.mvp.e.MagnetSearchBean
import cn.sddman.download.mvp.m.DownLoadModel
import cn.sddman.download.mvp.m.DownLoadModelImp
import cn.sddman.download.mvp.m.TaskModel
import cn.sddman.download.mvp.m.TaskModelImp
import cn.sddman.download.mvp.v.SourceView

class SourcePresenterImp(val sourceView: SourceView) : SourcePresenter {
    private val taskModel: TaskModel
    private val downLoadModel: DownLoadModel

    init {
        taskModel = TaskModelImp()
        downLoadModel = DownLoadModelImp()
    }

    override fun searchMagnet(rule: MagnetRule, keyword: String, page: Int) {
        IAsyncTask(sourceView).executeOnExecutor(Const.THREAD_POOL_EXECUTOR,MagnetSearchBean(rule,keyword,page,""))
    }


    class IAsyncTask(val sourceView: SourceView) : AsyncTask<MagnetSearchBean, Int, List<MagnetInfo>>() {
        override fun doInBackground(vararg params: MagnetSearchBean): List<MagnetInfo> {
            val bean = params[0]
            val fetchInf = Class.forName(bean.rule.parserClass).newInstance() as MagnetFetchInf
            var list = listOf<MagnetInfo>()
            try {
                list = fetchInf.parser(bean.rule, bean.keyword, bean.page);
            }catch (e:Exception){
                e.printStackTrace()
            }
            return list
        }

        override fun onPostExecute(result: List<MagnetInfo>) {
            sourceView.refreshData(result);
        }

    }
}
