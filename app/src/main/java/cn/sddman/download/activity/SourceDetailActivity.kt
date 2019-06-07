package cn.sddman.download.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.sddman.download.R
import cn.sddman.download.adapter.SourceDetailListAdapter
import cn.sddman.download.adapter.TorrentInfoAdapter
import cn.sddman.download.common.BaseActivity
import cn.sddman.download.common.Const
import cn.sddman.download.common.MessageEvent
import cn.sddman.download.common.Msg
import cn.sddman.download.listener.GetThumbnailsListener
import cn.sddman.download.mvp.e.MagnetDetail
import cn.sddman.download.mvp.e.MagnetInfo
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.mvp.e.TorrentInfoEntity
import cn.sddman.download.mvp.p.*
import cn.sddman.download.mvp.v.SourceDetailView
import cn.sddman.download.mvp.v.TorrentInfoView
import cn.sddman.download.thread.GetTorrentVideoThumbnailsTask
import cn.sddman.download.util.AlertUtil
import cn.sddman.download.util.Util
import kotlinx.android.synthetic.main.activity_torrent_info.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class SourceDetailActivity : BaseActivity(), SourceDetailView {
    private var detailUrl: String? = null
    private lateinit var magnetRule:MagnetRule
    private lateinit var sourceDetailListAdapter: SourceDetailListAdapter

    private var list = arrayListOf<String>()

    companion object {
        val DETAIL_URL:String = "detail_url"
        val TITLE:String = "title"
        val MAGNET_RULE:String = "magnet_rule"
    }
    override fun refreshData(magnetDetail: MagnetDetail?) {
        list?.clear()
        list?.addAll(magnetDetail!!.link!!)
        sourceDetailListAdapter?.notifyDataSetChanged()
    }

    override fun clickItem(url: String) {


    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source_detail)
        val getIntent = intent
        detailUrl = getIntent.getStringExtra(DETAIL_URL)
        val title = getIntent.getStringExtra(TITLE)
        magnetRule = getIntent.getParcelableExtra(MAGNET_RULE)
        setTopBarTitle(title)
        val sourceDetailPresenterImp = SourceDetailPresenterImp(this)
        sourceDetailPresenterImp.parser(magnetRule,detailUrl!!)
        initRV()
    }



    private fun initRV(){
        val manager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        recyclerview!!.layoutManager = manager
        sourceDetailListAdapter = SourceDetailListAdapter(this, this, this.list!!)
        recyclerview.adapter = sourceDetailListAdapter
    }
}


