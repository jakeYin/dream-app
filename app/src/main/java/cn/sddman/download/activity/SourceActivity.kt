package cn.sddman.download.activity

import android.content.Intent
import android.os.Bundle
import cn.sddman.download.R
import cn.sddman.download.adapter.SourceFrmAdapter
import cn.sddman.download.common.BaseActivity
import cn.sddman.download.common.Const
import cn.sddman.download.common.CusAdapter
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.service.DownService
import cn.sddman.download.util.GsonUtil
import cn.sddman.download.util.Util
import cn.sddman.download.view.SourceFrm
import kotlinx.android.synthetic.main.activity_source.*
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.SECONDS



class SourceActivity : BaseActivity() {

    private val mFragments = ArrayList<SourceFrm>()
    private val mTitles = ArrayList<String>()
    private var rules: List<MagnetRule>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source)
        val intent = Intent(this, DownService::class.java)
        startService(intent)
        initViewPage()
    }

    private fun initViewPage() {
        loadRules();
        val tabs = arrayListOf<String>()
        for (rule in rules!!){
            val bundle = Bundle()
            bundle.putParcelable(SourceFrm.MAGNET_RULE,rule);
            val sourceFrm = SourceFrm.newInstance(bundle)
            mFragments.add(sourceFrm)
            tabs.add(rule.site)
        }
        source_vp!!.offscreenPageLimit = rules!!.size
        source_vp.adapter = SourceFrmAdapter(supportFragmentManager, mFragments,tabs)
        tab_sources.setupWithViewPager(source_vp)
    }

    private fun loadRules() {
        rules = GsonUtil.getRule(this, "rule.json")
        if (rules == null) {
            Util.alert(this, "获取种子来源网站失败，请重新打开本页面或者重启APP", Const.ERROR_ALERT)
            return
        }
    }
}
