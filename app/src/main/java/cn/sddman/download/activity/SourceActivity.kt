package cn.sddman.download.activity

import android.content.Intent
import android.os.Bundle
import cn.sddman.download.R
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

class SourceActivity : BaseActivity() {
    private val mFragments = ArrayList<SourceFrm>()
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
        for (rule in rules!!){
            val bundle = Bundle()
            bundle.putParcelable(SourceFrm.MAGNET_RULE,rule);
            val sourceFrm = SourceFrm.newInstance(bundle)
            mFragments.add(sourceFrm)
        }
        source_vp!!.offscreenPageLimit = rules!!.size
        source_vp.adapter = CusAdapter(supportFragmentManager, mFragments)
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
