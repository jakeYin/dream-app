package cn.sddman.download.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.sddman.download.R
import cn.sddman.download.adapter.SourceDetailListAdapter
import cn.sddman.download.common.BaseActivity
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.MagnetDetail
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.mvp.p.SourceDetailPresenterImp
import cn.sddman.download.mvp.p.UrlDownLoadPresenterImp
import cn.sddman.download.mvp.v.SourceDetailView
import cn.sddman.download.mvp.v.UrlDownLoadView
import cn.sddman.download.util.AdUtil
import cn.sddman.download.util.SharedPreferencesUtils
import cn.sddman.download.util.Util
import kotlinx.android.synthetic.main.activity_source_detail.*

class SourceDetailActivity : BaseActivity(), SourceDetailView, UrlDownLoadView {
    private var detailUrl: String? = null
    private lateinit var magnetRule: MagnetRule
    private lateinit var sourceDetailListAdapter: SourceDetailListAdapter
    private var linkList = arrayListOf<MagnetDetail>()
    private lateinit var urlDownLoadPresenter: UrlDownLoadPresenterImp
    companion object {
        val DETAIL_URL: String = "detail_url"
        val TITLE: String = "title"
        val MAGNET_RULE: String = "magnet_rule"
    }

    override fun refreshData(list: List<MagnetDetail>) {
        linkList.clear()
        linkList.addAll(list)
        sourceDetailListAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source_detail)
        detailUrl = intent.getStringExtra(DETAIL_URL)
        val title = intent.getStringExtra(TITLE)
        magnetRule = intent.getParcelableExtra(MAGNET_RULE)
        title?.let { setTopBarTitle(it) }
        val sourceDetailPresenterImp = SourceDetailPresenterImp(this)
        urlDownLoadPresenter = UrlDownLoadPresenterImp(this)
        sourceDetailPresenterImp.parser(magnetRule, detailUrl!!)
        if (AdUtil.AD_TYPE == AdUtil.AD_TYPE_BANNER){
            AdUtil.showBannerAd(ad_banner_view)
        }
        initRV()
    }

    private fun initRV() {
        val manager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        recyclerview!!.layoutManager = manager
        sourceDetailListAdapter = SourceDetailListAdapter(this, this, this.linkList)
        recyclerview.adapter = sourceDetailListAdapter
    }

    fun selectAllClick(v: View) {
        for (x in linkList) {
            x.check = true
        }
        sourceDetailListAdapter.notifyDataSetChanged()
    }

    fun downloadSelectedClick(v: View) {
        if (AdUtil.AD_TYPE == AdUtil.AD_TYPE_BANNER){
            var count = SharedPreferencesUtils.getReward(this)
            for (x in linkList) {
                if (x.check) {
                    count--
                    urlDownLoadPresenter.startTask(x.name, detailUrl, magnetRule.id)
                }
            }
        } else {
            if (SharedPreferencesUtils.getReward(this) > 0 || !AdUtil.isLoaded()) {
                var count = SharedPreferencesUtils.getReward(this)
                for (x in linkList) {
                    if (x.check) {
                        count--
                        urlDownLoadPresenter.startTask(x.name, detailUrl, magnetRule.id)
                    }
                }
                SharedPreferencesUtils.updateReward(this,count)
            } else{
                AdUtil.showRewardAd()
            }
        }

    }

    override fun addTaskSuccess() {
        Util.alert(this, "添加任务成功", Const.SUCCESS_ALERT)
    }

    override fun addTaskFail(msg: String) {
        Util.alert(this, msg, Const.ERROR_ALERT)
    }

    public override fun onPause() {
        super.onPause()
//        AdUtil.pause(this)
    }

    public override fun onResume() {
        super.onResume()
//        AdUtil.resume(this)
    }

}


