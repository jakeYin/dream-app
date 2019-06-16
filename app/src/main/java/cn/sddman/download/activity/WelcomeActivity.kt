package cn.sddman.download.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import cn.sddman.download.R
import cn.sddman.download.common.RuntimeRationale
import cn.sddman.download.mvp.p.AppConfigPresenterImp
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission

class WelcomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermission()
        } else {
            goHome()
        }

    }

    private fun goHome() {
        val appConfigPresenter = AppConfigPresenterImp()
        appConfigPresenter.getMagnetWebRule()
        val intent = Intent(this@WelcomeActivity, DownloadManagementActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.CAMERA,
                        Permission.READ_PHONE_STATE)
                .rationale(RuntimeRationale())
                .onGranted {
                    //Toast.makeText(WelcomeActivity.this, R.string.successfully, Toast.LENGTH_SHORT).show();
                    goHome()
                }
                .onDenied { permissions ->
                    // toast(R.string.failure);
                    if (AndPermission.hasAlwaysDeniedPermission(this@WelcomeActivity, permissions)) {
                        showSettingDialog(this@WelcomeActivity, permissions)
                    }
                }
                .start()
    }

    fun showSettingDialog(context: Context, permissions: List<String>) {
        val permissionNames = Permission.transformText(context, permissions)
        val message = context.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames))

        AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.title_dialog)
                .setMessage(message)
                .setPositiveButton(R.string.setting) { dialog, which -> setPermission() }
                .setNegativeButton(R.string.cancel) { dialog, which -> }
                .show()
    }

    private fun setPermission() {
        AndPermission.with(this)
                .runtime()
                .setting()
                .onComeback { Toast.makeText(this@WelcomeActivity, R.string.message_setting_comeback, Toast.LENGTH_SHORT).show() }
                .start()
    }


}
