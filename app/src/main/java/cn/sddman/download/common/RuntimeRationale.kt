package cn.sddman.download.common

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.text.TextUtils

import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.Rationale
import com.yanzhenjie.permission.RequestExecutor

import cn.sddman.download.R

class RuntimeRationale : Rationale<List<String>> {

    override fun showRationale(context: Context, permissions: List<String>, executor: RequestExecutor) {
        val permissionNames = Permission.transformText(context, permissions)
        val message = context.getString(R.string.message_permission_rationale, TextUtils.join("\n", permissionNames))

        AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.title_dialog)
                .setMessage(message)
                .setPositiveButton(R.string.resume) { dialog, which -> executor.execute() }
                .setNegativeButton(R.string.cancel) { dialog, which -> executor.cancel() }
                .show()
    }
}