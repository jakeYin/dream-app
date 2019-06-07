package cn.sddman.download.activity

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.sddman.download.R
import cn.sddman.download.common.BaseActivity
import kotlinx.android.synthetic.main.activity_browse.*
import net.steamcrafted.loadtoast.LoadToast

class BrowseActivity : BaseActivity() {

    private var lt: LoadToast? = null
    private var btUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)
        super.setTopBarTitle("详情页面")
        val getIntent = intent
        btUrl = getIntent.getStringExtra("url")
        lt = LoadToast(this)
        web_view!!.webChromeClient = MyWebChromeClient()
        val webSettings = web_view.settings
        webSettings.setAppCacheEnabled(true)
        webSettings.domStorageEnabled = true
        webSettings.supportMultipleWindows()
        webSettings.allowContentAccess = true
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.savePassword = true
        webSettings.saveFormData = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        webSettings.javaScriptEnabled = false
        web_view.webViewClient = WebViewClient()
        web_view.loadUrl(btUrl)
        lt!!.setTranslationY(200).setBackgroundColor(resources.getColor(R.color.colorMain)).setProgressColor(resources.getColor(R.color.white))
        lt!!.show()
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (100 == newProgress) {
                lt!!.success()
                lt!!.hide()
            }
        }
    }

}
