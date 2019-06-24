package com.dream.tlj.util

import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.provider.Settings
import com.dream.tlj.R
import com.dream.tlj.common.Const
import org.xutils.x

class SystemConfig private constructor() {
    private val audioManager: AudioManager
    private var contentResolver: ContentResolver? = null
    private var batteryInfoIntent: Intent? = null

    val streamMaxVolume: Int
        get() = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    var streamVolume: Int
        get() = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        set(audio) = audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audio, 0)

    val screenBrightness: Int
        get() {
            contentResolver = x.app().applicationContext.contentResolver
            val defVal = 125
            return Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS, defVal)
        }

    /**
     * 实时获取电量
     */
    val systemBattery: Int
        get() {
            batteryInfoIntent = x.app().applicationContext.registerReceiver(null,
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            var level = 0
            level = batteryInfoIntent!!.getIntExtra("level", 0)
            val batterySum = batteryInfoIntent!!.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
            return 100 * level / batterySum
        }
    val systemBatteryStatus: Boolean?
        get() {
            batteryInfoIntent = x.app().applicationContext.registerReceiver(null,
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val state = batteryInfoIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            return state == BatteryManager.BATTERY_STATUS_CHARGING
        }

    val batteryIcon: Int
        get() {
            if (systemBatteryStatus!!) {
                return R.drawable.ic_battery_charg
            } else if (systemBattery >= 100) {
                return R.drawable.ic_battery_100
            } else if (systemBattery >= 80) {
                return R.drawable.ic_battery_80
            } else if (systemBattery >= 60) {
                return R.drawable.ic_battery_60
            } else if (systemBattery >= 40) {
                return R.drawable.ic_battery_40
            } else if (systemBattery >= 10) {
                return R.drawable.ic_battery_20
            } else if (systemBattery >= 0) {
                return R.drawable.ic_battery_0
            }
            return 0
        }

    init {
        audioManager = x.app().getSystemService(Application.AUDIO_SERVICE) as AudioManager
        //setScrennManualMode();
    }

    fun setScrennManualMode() {
        try {
            contentResolver = x.app().applicationContext.contentResolver
            val mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE)
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
            }
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }

    }

    fun setWindowBrightness(activity: Activity, brightness: Int) {
        val window = activity.window
        val lp = window.attributes
        lp.screenBrightness = brightness / 255.0f
        window.attributes = lp
    }

    companion object {
        private var systemConfig: SystemConfig? = null

        val instance: SystemConfig
            @Synchronized get() {
                if (systemConfig == null) {
                    systemConfig = SystemConfig()
                }
                return systemConfig!!
            }

        val netType: Int
            get() {
                val connectivityManager = x.app().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        ?: return Const.NET_TYPE_UNKNOW
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                        ?: return Const.NET_TYPE_UNKNOW
                val type = activeNetworkInfo.type
                if (type == ConnectivityManager.TYPE_WIFI) {
                    return Const.NET_TYPE_WIFI
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    return Const.NET_TYPE_MOBILE
                }
                return Const.NET_TYPE_UNKNOW
            }
    }
}
