package cn.sddman.download.mvp.p

import android.os.AsyncTask
import cn.sddman.bt.spider.MagnetFetchInf
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.MagnetDetail
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.mvp.e.MagnetSearchBean
import cn.sddman.download.mvp.v.SourceDetailView

class SourceDetailPresenterImp(val sourceView: SourceDetailView) : SourceDetailPresenter {

    override fun parser(magnetRule: MagnetRule, url: String) {
        IAsyncTask(sourceView).executeOnExecutor(Const.THREAD_POOL_EXECUTOR,MagnetSearchBean(magnetRule,url))
    }

    class IAsyncTask(val sourceView: SourceDetailView) : AsyncTask<MagnetSearchBean, Int, List<MagnetDetail>>() {
        override fun doInBackground(vararg params: MagnetSearchBean): List<MagnetDetail> {
            val bean = params[0]
            val fetchInf = Class.forName(bean.rule.parserDetailClass).newInstance() as MagnetFetchInf
            val detail = fetchInf.parser(bean.rule, bean.detailUrl);
            return detail
        }

        override fun onPostExecute(result: List<MagnetDetail>) {
            sourceView.refreshData(result)
        }

    }
}
