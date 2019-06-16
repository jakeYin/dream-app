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
import cn.sddman.download.rule.Rule
import cn.sddman.download.util.GsonUtil
import cn.sddman.download.util.Util
import cn.sddman.download.view.SourceFrm
import kotlinx.android.synthetic.main.activity_source.*
import kotlinx.android.synthetic.main.view_sear_bar.*
import java.util.*


class SourceActivity : BaseActivity() {

    companion object {
        val TYPE:String = "type"
        val TYPE_SOURCE:Int = 1
        val TYPE_SEARCH:Int = 2
    }

    private val mFragments = ArrayList<SourceFrm>()
    private var rules: List<MagnetRule>? = null
    private var path: String = ""
    private var type: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source)
        type = intent.getIntExtra(TYPE,-1)
//        val intent = Intent(this, DownService::class.java)
//        startService(intent)
        input_search.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.keyCode && KeyEvent.ACTION_DOWN == event.action)) {
                val adapter = source_vp.adapter as SourceFrmAdapter
                adapter.currentFragment.search(input_search.text?.trim().toString())
            }
            false
        }
        if (type == TYPE_SOURCE){
            input_search.setText("电影天堂")
            input_search.isFocusable = false
            rules = Rule.sourceRule
        } else if (type == TYPE_SEARCH){
            rules = Rule.searchRule
            input_search.setText("")
        }
        initViewPage()
    }

    fun btnSearchClick(v:View){
        if (type == TYPE_SEARCH){
            val adapter = source_vp.adapter as SourceFrmAdapter
            adapter.currentFragment.search(input_search.text?.trim().toString())
        } else {
            val intent = Intent(this@SourceActivity, SourceActivity::class.java)
            intent.putExtra(SourceActivity.TYPE,SourceActivity.TYPE_SEARCH)
            startActivity(intent)
        }
    }

    private fun initViewPage() {
        val tabs = arrayListOf<String>()
        for (rule in rules!!){
            val bundle = Bundle()
            bundle.putParcelable(SourceFrm.MAGNET_RULE,rule)
            bundle.putString(SourceFrm.KEY_WORD, input_search.text.toString())
            val sourceFrm = SourceFrm.newInstance(bundle)
            mFragments.add(sourceFrm)
            tabs += rule.site
        }
        source_vp!!.offscreenPageLimit = 2
        source_vp.adapter = SourceFrmAdapter(supportFragmentManager, mFragments,tabs)
        tab_sources.setupWithViewPager(source_vp)
    }
}
