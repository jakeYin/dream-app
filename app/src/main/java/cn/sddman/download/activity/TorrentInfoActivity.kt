package cn.sddman.download.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.sddman.download.R
import cn.sddman.download.adapter.TorrentInfoAdapter
import cn.sddman.download.common.BaseActivity
import cn.sddman.download.common.Const
import cn.sddman.download.common.MessageEvent
import cn.sddman.download.common.Msg
import cn.sddman.download.listener.GetThumbnailsListener
import cn.sddman.download.mvp.e.TorrentInfoEntity
import cn.sddman.download.mvp.p.TorrentInfoPresenter
import cn.sddman.download.mvp.p.TorrentInfoPresenterImp
import cn.sddman.download.mvp.v.TorrentInfoView
import cn.sddman.download.thread.GetTorrentVideoThumbnailsTask
import cn.sddman.download.util.AlertUtil
import cn.sddman.download.util.Util
import kotlinx.android.synthetic.main.activity_torrent_info.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class TorrentInfoActivity : BaseActivity(), TorrentInfoView {
    private var list: List<TorrentInfoEntity>? = null
    private val checkList = ArrayList<TorrentInfoEntity>()
    private var torrentInfoAdapter: TorrentInfoAdapter? = null
    private var torrentInfoPresenter: TorrentInfoPresenter? = null
    private var torrentPath: String? = null
    private val isCheckAll = false
    override var isDown = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torrent_info)
        setTopBarTitle(R.string.bt_file_info)
        //right_view.setText(R.string.check_all);
        val getIntent = intent
        torrentPath = getIntent.getStringExtra("torrentPath")
        isDown = getIntent.getBooleanExtra("isDown", false)
        if (isDown) {
            start_download!!.visibility = View.VISIBLE
        }
        torrentInfoPresenter = TorrentInfoPresenterImp(this, torrentPath!!)
    }

    override fun initTaskListView(list: List<TorrentInfoEntity>) {
        this.list = list
        val manager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        recyclerview!!.layoutManager = manager
        torrentInfoAdapter = TorrentInfoAdapter(this, this, this.list!!)
        recyclerview.adapter = torrentInfoAdapter
        if (!isDown) {
            AlertUtil.showLoading()
            GetTorrentVideoThumbnailsTask(object : GetThumbnailsListener {
                override fun success(bitmap: Bitmap?) {
                    torrentInfoAdapter!!.notifyDataSetChanged()
                    AlertUtil.hideLoading()
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list)
        }
    }

    override fun itemClick(index: Int) {
        //        TorrentInfoEntity torrent=list.get(index);
        //        if(torrent.getCheck()){
        //            torrent.setCheck(false);
        //            isCheckAll=false;
        //            right_view.setText(R.string.check_all);
        //        }else{
        //            torrent.setCheck(true);
        //        }
        //        torrentInfoAdapter.notifyDataSetChanged();
        //        checkList();
        //        setTopBarTitle(String.format(getString(R.string.check_count),list.size()+"",checkList.size()+""));

    }

    override fun startTaskSuccess() {
        EventBus.getDefault().postSticky(MessageEvent(Msg(Const.MESSAGE_TYPE_SWITCH_TAB, 0)))
        finish()
    }

    override fun startTaskFail(msg: String) {
        Util.alert(this, msg, Const.ERROR_ALERT)
    }

    override fun playVideo(te: TorrentInfoEntity) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(PlayerActivity.VIDEO_PATH, te.path)
        intent.putExtra(PlayerActivity.VIDEO_NAME, te.getmFileName())
        startActivity(intent)
    }

    private fun chheckAllClick(view: View) {
        //        for(TorrentInfoEntity torrent:list){
        //            torrent.setCheck(!isCheckAll);
        //        }
        //        torrentInfoAdapter.notifyDataSetChanged();
        //        if(isCheckAll){
        //            isCheckAll=false;
        //            right_view.setText(R.string.check_all);
        //            setTopBarTitle(R.string.check_file);
        //        }else{
        //            isCheckAll=true;
        //            right_view.setText(R.string.cancel_check_all);
        //            setTopBarTitle(String.format(getString(R.string.check_count),list.size()+"",list.size()+""));
        //        }
    }

    fun startDownClick(view: View) {
        checkList()
        torrentInfoPresenter!!.startTask(checkList)
        //finish();
    }

    private fun checkList() {
        checkList.clear()
        for (torrent in list!!) {
            if (torrent.check!!) {
                checkList.add(torrent)
            }
        }
    }

}
