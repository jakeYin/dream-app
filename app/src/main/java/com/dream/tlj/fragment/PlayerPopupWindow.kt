package com.dream.tlj.fragment

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.dream.tlj.R
import com.dream.tlj.common.Const
import com.dream.tlj.util.StringUtil
import com.dream.tlj.util.SystemConfig
import com.dream.tlj.util.Util
import com.aplayer.aplayerandroid.APlayerAndroid

class PlayerPopupWindow(private val parentActivity: Activity, parentView: View, aPlayer: APlayerAndroid) : PopupWindow(parentView.height, parentView.height), View.OnClickListener {
    private var mRootView: View? = null
    private var parentView: View? = null
    private var aPlayer: APlayerAndroid? = null
    private var fullScreen: TextView? = null
    private var nativeScreen: TextView? = null
    private var playSpeedText: TextView? = null
    private var playAudioText: TextView? = null
    private var playSpeedPlus: ImageView? = null
    private var playSpeedCut: ImageView? = null
    private var playAudioPlus: ImageView? = null
    private var playAudioCut: ImageView? = null
    private var playBrightnessCut: ImageView? = null
    private var playBrightnessPlus: ImageView? = null
    private var playAudioSeek: SeekBar? = null
    private var playBrightnessSeek: SeekBar? = null
    private var playDecoder: Switch? = null

    init {
        this.parentView = parentView
        this.aPlayer = aPlayer
        mRootView = LayoutInflater.from(parentView.context).inflate(R.layout.view_video_more_setting, null)
        contentView = mRootView
        initView()
    }

    private fun initView() {
        playSpeedText = mRootView!!.findViewById<View>(R.id.play_speed_text) as TextView
        fullScreen = mRootView!!.findViewById<View>(R.id.full_screen) as TextView
        nativeScreen = mRootView!!.findViewById<View>(R.id.native_screen) as TextView
        //playAudioText=(TextView)mRootView.findViewById(R.id.play_audio_text);
        playSpeedPlus = mRootView!!.findViewById<View>(R.id.play_speed_plus) as ImageView
        playSpeedCut = mRootView!!.findViewById<View>(R.id.play_speed_cut) as ImageView
        playAudioPlus = mRootView!!.findViewById<View>(R.id.play_audio_plus) as ImageView
        playAudioCut = mRootView!!.findViewById<View>(R.id.play_audio_cut) as ImageView
        playBrightnessCut = mRootView!!.findViewById<View>(R.id.play_brightness_cut) as ImageView
        playBrightnessPlus = mRootView!!.findViewById<View>(R.id.play_brightness_plus) as ImageView
        playAudioSeek = mRootView!!.findViewById<View>(R.id.play_audio_seek) as SeekBar
        playBrightnessSeek = mRootView!!.findViewById<View>(R.id.play_brightness_seek) as SeekBar
        playDecoder = mRootView!!.findViewById<View>(R.id.play_decoder) as Switch
        //playAudioText.setText(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)+"");
        playSpeedPlus!!.setOnClickListener(this)
        playSpeedCut!!.setOnClickListener(this)
        playAudioPlus!!.setOnClickListener(this)
        playAudioCut!!.setOnClickListener(this)
        playBrightnessPlus!!.setOnClickListener(this)
        playBrightnessCut!!.setOnClickListener(this)
        fullScreen!!.setOnClickListener(this)
        nativeScreen!!.setOnClickListener(this)

        val barChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                when (seekBar.id) {
                    R.id.play_audio_seek -> SystemConfig.instance.streamVolume = i
                    R.id.play_brightness_seek -> SystemConfig.instance.setWindowBrightness(parentActivity, i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        }
        val bool = aPlayer!!.getConfig(APlayerAndroid.CONFIGID.HW_DECODER_ENABLE)
        playDecoder!!.isChecked = bool == "1"
        playDecoder!!.setOnCheckedChangeListener { compoundButton, b ->
            val decoder = aPlayer!!.getConfig(APlayerAndroid.CONFIGID.HW_DECODER_ENABLE)
            if ("0" == decoder) {
                Util.alert(parentActivity, "当前手机不支持硬件解码", Const.ERROR_ALERT)
                playDecoder!!.isChecked = false
            } else {
                playDecoder!!.isChecked = b
                aPlayer?.setConfig(APlayerAndroid.CONFIGID.HW_DECODER_USE, if (b) "1" else "0")
            }
        }
        playAudioSeek!!.setOnSeekBarChangeListener(barChangeListener)
        playBrightnessSeek!!.setOnSeekBarChangeListener(barChangeListener)
        setSystemPlayAudio()
        setSystemBrightness()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.play_speed_plus -> {
                var speed: Double = StringUtil.stringToDouble(playSpeedText!!.text.toString())
                if (speed >= 2) return
                speed += 0.1
                setPlaySpeed(StringUtil.stringToDouble(speed!!.toString() + ""))
                playSpeedText!!.text = StringUtil.stringToDouble(speed.toString() + "").toString() + ""
            }
            R.id.play_speed_cut -> {
                var speed2: Double = StringUtil.stringToDouble(playSpeedText!!.text.toString())
                if (speed2 <= 0.5) return
                speed2 -= 0.1
                setPlaySpeed(StringUtil.stringToDouble(speed2.toString() + ""))
                playSpeedText!!.text = StringUtil.stringToDouble(speed2.toString() + "").toString() + ""
            }
            R.id.play_audio_plus -> {
                val currentVolume = SystemConfig.instance.streamVolume
                if (currentVolume >= SystemConfig.instance.streamMaxVolume) return
                SystemConfig.instance.streamVolume = currentVolume + 2
                setSystemPlayAudio()
            }
            R.id.play_audio_cut -> {
                val currentVolume2 = SystemConfig.instance.streamVolume
                if (currentVolume2 <= 0) return
                SystemConfig.instance.streamVolume = currentVolume2 - 2
                setSystemPlayAudio()
            }
            R.id.play_brightness_plus -> {
                var currBrightness = playBrightnessSeek!!.progress
                if (currBrightness >= 255) return
                currBrightness = if (currBrightness >= 240) 255 else currBrightness + 15
                playBrightnessSeek!!.progress = currBrightness
                SystemConfig.instance.setWindowBrightness(parentActivity, currBrightness)
            }
            R.id.play_brightness_cut -> {
                var currBrightness2 = playBrightnessSeek!!.progress
                if (currBrightness2 <= 0) return
                currBrightness2 = if (currBrightness2 < 15) 0 else currBrightness2 - 15
                playBrightnessSeek!!.progress = currBrightness2
                SystemConfig.instance.setWindowBrightness(parentActivity, currBrightness2)
            }
            R.id.full_screen -> {
                val parm = parentView!!.width.toString() + ";" + parentView?.height
                aPlayer!!.setConfig(APlayerAndroid.CONFIGID.ASPECT_RATIO_CUSTOM, parm)
                fullScreen!!.setTextColor(parentActivity.resources.getColor(R.color.colorMain))
                nativeScreen!!.setTextColor(parentActivity.resources.getColor(R.color.gray_8f))
            }
            R.id.native_screen -> {
                val parm2 = aPlayer!!.getConfig(APlayerAndroid.CONFIGID.ASPECT_RATIO_NATIVE)
                aPlayer?.setConfig(APlayerAndroid.CONFIGID.ASPECT_RATIO_CUSTOM, parm2)
                nativeScreen!!.setTextColor(parentActivity.resources.getColor(R.color.colorMain))
                fullScreen!!.setTextColor(parentActivity.resources.getColor(R.color.gray_8f))
            }
        }//setSystemBrightness();
        //setSystemBrightness();
    }

    private fun setPlaySpeed(speed: Double?) {
        aPlayer!!.setConfig(APlayerAndroid.CONFIGID.PLAY_SPEED, (speed!! * 100).toString() + "")
    }

    fun setSystemPlayAudio() {
        val maxVolume = SystemConfig.instance.streamMaxVolume
        val currentVolume = SystemConfig.instance.streamVolume
        playAudioSeek!!.max = maxVolume
        playAudioSeek!!.progress = currentVolume
    }

    fun setSystemBrightness() {
        val currentBrightness = SystemConfig.instance.screenBrightness
        playBrightnessSeek!!.max = 255
        playBrightnessSeek!!.progress = currentBrightness
    }
}
