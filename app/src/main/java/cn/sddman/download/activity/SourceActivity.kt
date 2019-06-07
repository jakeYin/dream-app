package cn.sddman.download.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import cn.sddman.download.R
import cn.sddman.download.adapter.SourceFrmAdapter
import cn.sddman.download.common.BaseActivity
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.service.DownService
import cn.sddman.download.util.GsonUtil
import cn.sddman.download.util.Util
import cn.sddman.download.view.SourceFrm
import kotlinx.android.synthetic.main.activity_source.*
import kotlinx.android.synthetic.main.view_sear_bar.*
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

        input_search.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.keyCode && KeyEvent.ACTION_DOWN == event.action)) {
                val adapter = source_vp.adapter as SourceFrmAdapter
                adapter.currentFragment.search(input_search.text?.trim().toString())
            }
            false
        }
    }

    fun btnSearchClick(v:View){
        val adapter = source_vp.adapter as SourceFrmAdapter
        adapter.currentFragment.search(input_search.text?.trim().toString())
    }

    private fun initViewPage() {
        loadRules()
        val tabs = arrayListOf<String>()
        for (rule in rules!!){
            val bundle = Bundle()
            bundle.putParcelable(SourceFrm.MAGNET_RULE,rule)
            val sourceFrm = SourceFrm.newInstance(bundle)
            mFragments.add(sourceFrm)
            tabs += rule.site
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
