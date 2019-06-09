package cn.sddman.download.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.sddman.download.R
import cn.sddman.download.activity.SourceDetailActivity
import cn.sddman.download.adapter.SourceListAdapter
import cn.sddman.download.common.Const
import cn.sddman.download.common.RecyclerViewNoBugLinearLayoutManager
import cn.sddman.download.mvp.e.MagnetInfo
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.mvp.p.SourcePresenterImp
import cn.sddman.download.mvp.p.UrlDownLoadPresenterImp
import cn.sddman.download.mvp.v.SourceView
import cn.sddman.download.mvp.v.UrlDownLoadView
import cn.sddman.download.util.StringUtil
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
    private var keyword: String = ""
    private lateinit var searchListAdapter: SourceListAdapter
    private var searchPage: Int = 0
    companion object {
        fun newInstance(args: Bundle): SourceFrm {
            val fragment = SourceFrm()
            fragment.arguments = args
            return fragment
        }
        const val MAGNET_RULE = "magnet_rule"
        const val KEY_WORD = "key_word"
    }

    override fun refreshData(info: List<MagnetInfo>?) {
        source_twinklingRefreshLayout?.finishRefreshing()
        source_twinklingRefreshLayout?.finishLoadmore()
        if (null == info) {
            activity?.let { Util.alert(it, "网络超时，请重试", Const.ERROR_ALERT) }
        } else if (info.isEmpty()) {
            activity?.let { Util.alert(it, "没有更多了", Const.ERROR_ALERT) }
        } else {
            list.addAll(info)
            searchListAdapter.notifyDataSetChanged()
        }
    }

    override fun addTaskSuccess() {
        activity?.let { Util.alert(it, getString(R.string.add_task_success), Const.SUCCESS_ALERT) }
    }

    override fun addTaskFail(msg: String) {
        activity?.let { Util.alert(it, msg, Const.ERROR_ALERT) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frm_source, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rule = arguments?.getParcelable(MAGNET_RULE) as MagnetRule
        keyword = arguments?.getString(KEY_WORD).toString()
        magnetSearchPresenter = SourcePresenterImp(this)
        urlDownLoadPresenter = UrlDownLoadPresenterImp(this)
        initView()
        loadData()
    }



    private fun initView(){
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
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout) {
                searchPage = 0
                list.clear()
                searchListAdapter.notifyDataSetChanged()
                loadData()
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout) {
                searchPage += 1
                loadData()
            }
        })
    }
    private fun loadData() {
        if (!StringUtil.isEmpty(keyword)){
            magnetSearchPresenter.searchMagnet(rule,keyword,searchPage)
        }
    }

    override fun clickItem(magnet: MagnetInfo) {
        val intent = Intent(activity, SourceDetailActivity::class.java)
        intent.putExtra(SourceDetailActivity.DETAIL_URL, magnet.detailUrl)
        intent.putExtra(SourceDetailActivity.TITLE, magnet.name)
        intent.putExtra(SourceDetailActivity.MAGNET_RULE, rule)
        startActivity(intent)
//        BottomSheet.Builder(activity!!)
//                .title(R.string.slest_option)
//                .sheet(R.menu.magnet_option)
//                .listener(object : DialogInterface.OnClickListener {
//                    override fun onClick(dialog: DialogInterface, which: Int) {
//                        when (which) {
//                            R.id.down -> urlDownLoadPresenter.startTask(magnet.magnet!!)
//                            R.id.copy -> Util.putTextIntoClip(magnet.magnet!!)
////                            R.id.xl -> openXL(magnet)
//                            R.id.sourcepage -> {
//                                val intent = Intent(activity, BrowseActivity::class.java)
//                                intent.putExtra("url", magnet.detailUrl)
//                                startActivity(intent)
//                            }
//                        }
//                    }
//                }).show()
    }

    fun search(keyword: String) {
        this.keyword = keyword
        searchPage = 0
        list.clear()
        loadData()
    }


}
