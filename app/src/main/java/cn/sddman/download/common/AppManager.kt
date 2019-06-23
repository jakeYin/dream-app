package cn.sddman.download.common

import android.app.Activity
import android.content.Context
import java.util.*

/**
 * Activity管理类：用于管理Activity和退出程序
 */
class AppManager private constructor() {

    init {
        activityStack = Stack()
    }

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity {
        return activityStack.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        val activity = activityStack.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
        var activity = activity
        if (activity != null) {
            activityStack.remove(activity)
            activity.finish()
            activity = null
        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
                break
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        for (i in activityStack.indices) {
            if (null != activityStack[i]) {
                activityStack[i].finish()
            }
        }
        activityStack.clear()
    }

    /**
     * 退出应用程序
     */
    fun AppExit(context: Context) {
        try {
            finishAllActivity()
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        } catch (e: Exception) {
        }

    }

    companion object {

        // Activity栈
        private lateinit var activityStack: Stack<Activity>
        // 单例模式
        private var instance: AppManager? = null

        /**
         * 单一实例
         */
        val appManager: AppManager
            @Synchronized get() {
                if (instance == null) {
                    instance = AppManager()
                }
                return instance!!
            }
    }
}

