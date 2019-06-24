package com.dream.tlj.mvp.p

import android.os.AsyncTask
import com.dream.tlj.common.Const
import com.dream.tlj.mvp.e.MagnetInfo
import com.dream.tlj.mvp.e.MagnetRule
import com.dream.tlj.mvp.e.MagnetSearchBean
import com.dream.tlj.mvp.m.DownLoadModel
import com.dream.tlj.mvp.m.DownLoadModelImp
import com.dream.tlj.mvp.m.TaskModel
import com.dream.tlj.mvp.m.TaskModelImp
import com.dream.tlj.mvp.v.SourceView
import com.dream.tlj.spider.MagnetFetchInf

class SourcePresenterImp(val sourceView: SourceView) : SourcePresenter {
    private val taskModel: TaskModel
    private val downLoadModel: DownLoadModel

    init {
        taskModel = TaskModelImp()
        downLoadModel = DownLoadModelImp()
    }

    override fun searchMagnet(rule: com.dream.tlj.mvp.e.MagnetRule, keyword: String, page: Int) {
        IAsyncTask(sourceView).executeOnExecutor(Const.THREAD_POOL_EXECUTOR,MagnetSearchBean(rule,keyword,page,""))
    }


    class IAsyncTask(val sourceView: SourceView) : AsyncTask<MagnetSearchBean, Int, List<com.dream.tlj.mvp.e.MagnetInfo>>() {
        override fun doInBackground(vararg params: MagnetSearchBean): List<com.dream.tlj.mvp.e.MagnetInfo> {
            val bean = params[0]
            val fetchInf = Class.forName(bean.rule.parserClass).newInstance() as MagnetFetchInf
            var list = listOf<com.dream.tlj.mvp.e.MagnetInfo>()
            try {
                list = fetchInf.parser(bean.rule, bean.keyword, bean.page);
            }catch (e:Exception){
                e.printStackTrace()
            }
            return list
        }

        override fun onPostExecute(result: List<com.dream.tlj.mvp.e.MagnetInfo>) {
            sourceView.refreshData(result);
        }

    }
}
