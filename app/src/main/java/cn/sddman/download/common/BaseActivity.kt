package cn.sddman.download.common

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.coorchice.library.SuperTextView
import kotlinx.android.synthetic.main.view_top_bar.*


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.appManager.addActivity(this)
    }

     fun setTopBarTitle(title: Int) {
        topBarTitle!!.setText(title)
    }

     fun setTopBarTitle(title: String) {
        topBarTitle!!.text = title
    }

     fun setTopCloseText(text: String) {
        close_view!!.text = text
    }

     fun getRightView(): SuperTextView {
        return right_view
    }

     fun hideCloseView() {
        close_view!!.visibility = View.GONE
    }

     fun closeView(view: View) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.appManager.finishActivity(this)
    }
}
