package com.dream.tlj.common

import com.dream.tlj.R
import com.dream.tlj.util.FileTools
import org.xutils.x
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object Const {

    val DEBUG = false
    val URL_DOWNLOAD = 1
    val BT_DOWNLOAD = 2
    val APK_UPDATE_JSON_URL = "https://raw.githubusercontent.com/jakeYin/dream-app/master/release/update.json"

    val DB_NAME = "tljdownload.db"
    val DB_SDCARD_PATH = FileTools.sdCardPath + x.app().getString(R.string.app_db_path)
    val File_SAVE_PATH = FileTools.sdCardPath + x.app().getString(R.string.app_down_path)
    val File_CACHE_PATH = FileTools.sdCardPath + x.app().getString(R.string.app_cache_path)
    val VIDEO_PIC_PATH = FileTools.sdCardPath + x.app().getString(R.string.app_video_thumbnail_path)
    val APP_CRASH_PATH = FileTools.sdCardPath + x.app().getString(R.string.app_crash_path)

    //0连接中1下载中 2下载完成 3失败
    val DOWNLOAD_CONNECTION = 0
    val DOWNLOAD_LOADING = 1
    val DOWNLOAD_SUCCESS = 2
    val DOWNLOAD_FAIL = 3
    val DOWNLOAD_STOP = 4
    val DOWNLOAD_WAIT = 5

    //
    val SUCCESS_ALERT = 1
    val ERROR_ALERT = 2
    val WARNING_ALERT = 3

    //
    val MESSAGE_TYPE_BT_DOWN_SUCCESS = 1
    val MESSAGE_TYPE_REFRESH_DATA = 2
    val MESSAGE_TYPE_SWITCH_TAB = 3
    val MESSAGE_TYPE_RES_TASK = 4
    val MESSAGE_TYPE_APP_UPDATA = 5
    val MESSAGE_TYPE_APP_UPDATA_PRESS = 6

    //
    val MSG_HIDE = 0x01
    val UI_UPDATE_PLAY_STATION = 0X1000
    val TIMER_UPDATE_INTERVAL_TIME = 1000   //定时器时间间隔，更新播放进度

    val NET_TYPE_UNKNOW = 0
    val NET_TYPE_WIFI = 1
    val NET_TYPE_MOBILE = 2

    //
    val SAVE_PATH_KEY = "savepath"
    val MOBILE_NET_KEY = "mobilenet"
    val DOWN_COUNT_KEY = "downcount"
    val DOWN_NOTIFY_KEY = "downnotify"

    val MAX_DOWN_COUNT = 5
    val MIN_DOWN_COUNT = 1

    val MOBILE_NET_NOT = 0
    val MOBILE_NET_OK = 1

    val SEARCH_SORT_HOT = "hot"
    val SEARCH_SORT_DATE = "date"

    val THREAD_POOL_EXECUTOR: Executor = ThreadPoolExecutor(15, 200, 10, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>())
}
