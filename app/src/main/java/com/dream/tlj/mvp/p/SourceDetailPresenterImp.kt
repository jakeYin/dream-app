package com.dream.tlj.mvp.p

import android.os.AsyncTask
import com.dream.tlj.common.Const
import com.dream.tlj.mvp.e.MagnetDetail
import com.dream.tlj.mvp.e.MagnetRule
import com.dream.tlj.mvp.e.MagnetSearchBean
import com.dream.tlj.mvp.v.SourceDetailView
import com.dream.tlj.spider.MagnetFetchInf

class SourceDetailPresenterImp(val sourceView: SourceDetailView) : SourceDetailPresenter {

    override fun parser(magnetRule: com.dream.tlj.mvp.e.MagnetRule, url: String) {
        IAsyncTask(sourceView).executeOnExecutor(Const.THREAD_POOL_EXECUTOR,MagnetSearchBean(magnetRule,url))
    }

    class IAsyncTask(val sourceView: SourceDetailView) : AsyncTask<MagnetSearchBean, Int, List<com.dream.tlj.mvp.e.MagnetDetail>>() {
        override fun doInBackground(vararg params: MagnetSearchBean): List<com.dream.tlj.mvp.e.MagnetDetail> {
            val bean = params[0]
            val fetchInf = Class.forName(bean.rule.parserDetailClass).newInstance() as MagnetFetchInf
            val detail = fetchInf.parser(bean.rule, bean.detailUrl);
            return detail
        }

        override fun onPostExecute(result: List<com.dream.tlj.mvp.e.MagnetDetail>) {
            sourceView.refreshData(result)
        }

    }
}
