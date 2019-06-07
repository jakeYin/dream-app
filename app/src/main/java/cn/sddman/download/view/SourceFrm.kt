package cn.sddman.download.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.sddman.download.R
import cn.sddman.download.adapter.SourceListAdapter
import cn.sddman.download.common.Const
import cn.sddman.download.common.RecyclerViewNoBugLinearLayoutManager
import cn.sddman.download.mvp.e.MagnetInfo
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.mvp.p.SourcePresenterImp
import cn.sddman.download.mvp.p.UrlDownLoadPresenterImp
import cn.sddman.download.mvp.v.SourceView
import cn.sddman.download.mvp.v.UrlDownLoadView
import cn.sddman.download.util.Util
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import kotlinx.android.synthetic.main.frm_source.*
import java.util.*

class SourceFrm : Fragment(), SourceView, UrlDownLoadView {
    private val list = ArrayList<MagnetInfo>()
    private lateinit var rule: MagnetRule
    private lateinit var magnetSearchPresenter: SourcePresenterImp
    private lateinit var urlDownLoadPresenter: UrlDownLoadPresenterImp

    companion object {
        fun newInstance(args: Bundle): SourceFrm {
            val fragment = SourceFrm()
            fragment.arguments = args
            return fragment
        }
        const val MAGNET_RULE = "magnet_rule"
    }

    override fun refreshData(info: List<MagnetInfo>?) {
        source_twinklingRefreshLayout!!.finishRefreshing()
        source_twinklingRefreshLayout.finishLoadmore()
        if (null == info) {
            Util.alert(activity!!, "网络超时，请重试", Const.ERROR_ALERT)
        } else if (info.size == 0) {
            Util.alert(activity!!, "没有更多了", Const.ERROR_ALERT)
        } else {
            list.addAll(info)
            searchListAdapter!!.notifyDataSetChanged()
        }
    }

    override fun addTaskSuccess() {
        Util.alert(activity!!, getString(R.string.add_task_success), Const.SUCCESS_ALERT)
    }

    override fun addTaskFail(msg: String) {
        Util.alert(activity!!, msg, Const.ERROR_ALERT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frm_source, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rule = arguments?.getParcelable(MAGNET_RULE) as MagnetRule
        magnetSearchPresenter = SourcePresenterImp(this)
        urlDownLoadPresenter = UrlDownLoadPresenterImp(this)
        initView()
        loadData()
    }

    private lateinit var searchListAdapter: SourceListAdapter

    private var searchPage: Int = 0

    fun initView(){
        //recyclerView;
        source_rv!!.layoutManager = RecyclerViewNoBugLinearLayoutManager(context!!,
                LinearLayoutManager.VERTICAL, false)
        searchListAdapter = SourceListAdapter(context!!, this, list)
        source_rv.adapter = searchListAdapter
        //source_twinklingRefreshLayout
        val header = ProgressLayout(context)
        header.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.colorMain))
        header.setColorSchemeResources(R.color.white)
        source_twinklingRefreshLayout!!.setHeaderView(header)
        source_twinklingRefreshLayout.setFloatRefresh(true)
        source_twinklingRefreshLayout.setOverScrollRefreshShow(false)
        source_twinklingRefreshLayout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                searchPage = 0
                list.clear()
                searchListAdapter!!.notifyDataSetChanged()
                loadData()
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                searchPage += 1
                loadData()
            }
        })
    }

    private fun loadData() {
        magnetSearchPresenter.searchMagnet(rule,"妻子",searchPage)
    }

    override fun clickItem(magnet: MagnetInfo) {
        println("todo ====")
    }


}
