package cn.sddman.download.activity

import android.content.Context
import android.graphics.Point
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.*
import android.widget.SeekBar
import cn.sddman.download.R
import cn.sddman.download.common.BaseActivity
import cn.sddman.download.common.Const
import cn.sddman.download.fragment.PlayerPopupWindow
import cn.sddman.download.mvp.p.PlayerPresenter
import cn.sddman.download.mvp.p.PlayerPresenterImp
import cn.sddman.download.mvp.v.PlayerView
import cn.sddman.download.thread.HidePlayControl
import cn.sddman.download.thread.UpdataUIPlayHandler
import cn.sddman.download.thread.UpdatePlayUIProcess
import cn.sddman.download.util.*
import com.aplayer.aplayerandroid.APlayerAndroid
import kotlinx.android.synthetic.main.activity_player.*
import java.util.*


class PlayerActivity : BaseActivity(), PlayerView {

    private var videoPath: String = ""
    private var playerPresenter: PlayerPresenter? = null
    private var aPlayer: APlayerAndroid? = null
    private var mPopupWindow: PlayerPopupWindow? = null
    private var mUpdateThread: Thread? = null
    private var handle: Handler? = null
    private var hidePlayControl: HidePlayControl? = null
    @Volatile
    private var mIsTouchingSeekbar = false
    private var mIsNeedUpdateUIProgress = false   //标志位，是否需要更新播放进度
    private var mVisible = false
    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()


    private var videoName: String = ""

    companion object {
        val VIDEO_PATH: String = "video_path"
        val VIDEO_NAME: String = "video_name"
    }

    private var SCREEN_WIDTH: Int = 0
    private var SCREEN_HEIGHT: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val getIntent = intent
        videoPath = getIntent.getStringExtra(VIDEO_PATH)
        videoName = getIntent.getStringExtra(VIDEO_NAME)
        if (StringUtil.isEmpty(videoPath)) {
            Util.alert(this, "视频不存在", Const.ERROR_ALERT)
        } else {
            if (videoPath.startsWith("http://") || FileTools.exists(videoPath)) {
                playerPresenter = PlayerPresenterImp(this, videoPath, videoName)
            } else {
                Util.alert(this, "视频不存在", Const.ERROR_ALERT)
            }
        }
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        SCREEN_WIDTH = size.x
        SCREEN_HEIGHT = size.y
    }

    override fun initPlayer() {
        if (null != aPlayer) {
            return
        }
        handle = UpdataUIPlayHandler(this)
        hidePlayControl = HidePlayControl(this)
        hidePlayControl!!.startHideTimer()
        aPlayer = APlayerAndroid()
        aPlayer!!.setView(fullscreen_content)
        aPlayer!!.setOnOpenSuccessListener {
            playerPresenter!!.setHistoryCurrentPlayTimeMs()
            aPlayer!!.play()
            startUIUpdateThread()
        }
        aPlayer!!.setOnPlayCompleteListener { playRet ->
            val isUseCallStop = APlayerSationUtil.isStopByUserCall(playRet)
            if (!isUseCallStop) {
                finish()
            }
        }
        aPlayer!!.setOnOpenCompleteListener {
            // String s="";
        }
        registerListener()

        val decoder = aPlayer!!.getConfig(APlayerAndroid.CONFIGID.HW_DECODER_ENABLE)
        if ("1" == decoder) {
            aPlayer?.setConfig(APlayerAndroid.CONFIGID.HW_DECODER_USE, "1")
        }


    }

    private var mChangeVolume: Boolean = false
    private var mChangePosition: Boolean = false
    private var mChangeBrightness: Boolean = false
    private val THRESHOLD = 80
    private var mGestureDownPosition: Int = 0
    private var mGestureDownVolume: Int = 0
    private var mGestureDownBrightness: Float = 0.toFloat()
    private var mSeekTimePosition: Int = 0
    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0
    private fun registerListener() {
        mScreenWidth = resources.displayMetrics.widthPixels
        mScreenHeight = resources.displayMetrics.heightPixels
        val mAudioManager = applicationContext!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        play_seek_bar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private val SEEK_MIN_GATE_MS = 1000
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (null == aPlayer || !fromUser) {
                    return
                }
                mIsTouchingSeekbar = true
                userSeekPlayProgress(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mIsTouchingSeekbar = false
                startUIUpdateThread()
            }
        })
        val playViewClick = View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownX = motionEvent.x
                    mDownY = motionEvent.y
                    mChangeVolume = false
                    mChangePosition = false
                    mChangeBrightness = false
                    Handler().postDelayed({
                        if (!mChangeBrightness && !mChangePosition && !mChangeVolume) {
                            when (view.id) {
                                R.id.play_view_root -> if (!lock) {
                                    controlViewToggle()
                                } else {
                                    player_lock.visibility = View.VISIBLE
                                    hidePlayControl!!.resetHideTimer()
                                }
                                R.id.top_panel, R.id.bottom_panel -> if (!lock) controlViewShow()
                            }
                        }
                        destroyPopWind()
                    }, 100)
                }
                MotionEvent.ACTION_MOVE -> {
                    mIsTouchingSeekbar = true
                    if (!lock) {
                        val deltaX = motionEvent.x - mDownX
                        var deltaY = motionEvent.y - mDownY
                        val absDeltaX = Math.abs(deltaX)
                        val absDeltaY = Math.abs(deltaY)
                        if (!mChangePosition && !mChangeVolume && !mChangeBrightness) {
                            if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                                if (absDeltaX >= THRESHOLD) {
                                    mChangePosition = true
                                    mGestureDownPosition = aPlayer!!.position
                                } else {
                                    if (mDownX < mScreenWidth * 0.5f) {//左侧改变亮度
                                        mChangeBrightness = true
                                        val lp = window.attributes
                                        if (lp.screenBrightness < 0) {
                                            mGestureDownBrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS).toFloat()
                                        } else {
                                            mGestureDownBrightness = lp.screenBrightness * 255
                                        }
                                    } else {//右侧改变声音
                                        mChangeVolume = true
                                        mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                    }
                                }
                            }
                        }

                        if (mChangePosition) {
                            val totalTimeDuration = aPlayer!!.duration
                            mSeekTimePosition = (mGestureDownPosition + deltaX * 0.3 * totalTimeDuration / mScreenWidth).toInt()
                            if (mSeekTimePosition > totalTimeDuration)
                                mSeekTimePosition = totalTimeDuration
                            val seekTime = stringForTime(mSeekTimePosition)
                            val totalTime = stringForTime(totalTimeDuration)
                            showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration)
                        }
                        if (mChangeVolume) {
                            deltaY = -deltaY
                            val max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                            val deltaV = (max.toFloat() * deltaY * 3f / mScreenHeight).toInt()
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0)
                            val volumePercent = (mGestureDownVolume * 100 / max + deltaY * 3f * 100f / mScreenHeight).toInt()
                            showVolumeDialog(volumePercent)
                        }

                        if (mChangeBrightness) {
                            deltaY = -deltaY
                            val deltaV = (255f * deltaY * 3f / mScreenHeight).toInt()
                            val params = window.attributes
                            if ((mGestureDownBrightness + deltaV) / 255 >= 1) {//这和声音有区别，必须自己过滤一下负值
                                params.screenBrightness = 1f
                            } else if ((mGestureDownBrightness + deltaV) / 255 <= 0) {
                                params.screenBrightness = 0.01f
                            } else {
                                params.screenBrightness = (mGestureDownBrightness + deltaV) / 255
                            }
                            window.setAttributes(params)
                            //dialog中显示百分比
                            val brightnessPercent = (mGestureDownBrightness * 100 / 255 + deltaY * 3f * 100f / mScreenHeight).toInt()
                            showBrightnessDialog(brightnessPercent)
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    mIsTouchingSeekbar = false
                    if (!lock) {
                        volume_layout.visibility = View.GONE
                        brightness_layout.visibility = View.GONE
                        dialog_progress.visibility = View.GONE
                        if (mChangePosition) {
                            userSeekPlayProgress(mSeekTimePosition)
                        }
                        startUIUpdateThread()
                    }
                }
            }
            true
        }
        play_view_root.setOnTouchListener(playViewClick)
        top_panel.setOnTouchListener(playViewClick)
        bottom_panel.setOnTouchListener(playViewClick)
    }

    fun showProgressDialog(deltaX: Float, seekTime: String, seekTimePosition: Int, totalTime: String, totalTimeDuration: Int) {
        dialog_progress.visibility = View.VISIBLE
        tv_current.text = seekTime
        tv_duration.text = " / $totalTime"
        duration_progressbar.progress = if (totalTimeDuration <= 0) 0 else (seekTimePosition * 100 / totalTimeDuration)
        if (deltaX > 0) {
            duration_image_tip.setBackgroundResource(R.drawable.jz_forward_icon)
        } else {
            duration_image_tip.setBackgroundResource(R.drawable.jz_backward_icon)
        }
    }

    fun showVolumeDialog(volumePercent: Int) {
        volume_layout.visibility = View.VISIBLE
        var volumePercent = volumePercent

        if (volumePercent <= 0) {
            volume_image_tip.setBackgroundResource(R.drawable.jz_close_volume)
        } else {
            volume_image_tip.setBackgroundResource(R.drawable.jz_add_volume)
        }
        if (volumePercent > 100) {
            volumePercent = 100
        } else if (volumePercent < 0) {
            volumePercent = 0
        }
        tv_volume.text = volumePercent.toString() + "%"
        volume_progressbar.setProgress(volumePercent)
    }

    fun showBrightnessDialog(brightnessPercent: Int) {
        var brightnessPercent = brightnessPercent
        brightness_layout.visibility = View.VISIBLE
        if (brightnessPercent > 100) {
            brightnessPercent = 100
        } else if (brightnessPercent < 0) {
            brightnessPercent = 0
        }
        tv_brightness.text = brightnessPercent.toString() + "%"
        brightness_progressbar.setProgress(brightnessPercent)
    }

    fun stringForTime(timeMs: Int): String {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00"
        }
        val totalSeconds = timeMs / 1000
        val seconds = (totalSeconds % 60)
        val minutes = (totalSeconds / 60 % 60)
        val hours = (totalSeconds / 3600)
        val stringBuilder = StringBuilder()
        val mFormatter = Formatter(stringBuilder, Locale.getDefault())
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    private fun destroyPopWind() {
        if (null != mPopupWindow) {
            mPopupWindow!!.dismiss()
        }
    }

    override fun controlViewToggle() {
        if (mVisible && !mIsTouchingSeekbar) {
            controlViewHide()
        } else {
            controlViewShow()
        }
    }

    override fun controlViewShow() {
        if (null == aPlayer) return
        mVisible = true
        top_panel.visibility = View.VISIBLE
        bottom_panel.visibility = View.VISIBLE
        player_lock.visibility = View.VISIBLE
        hidePlayControl!!.resetHideTimer()
    }

    override fun controlViewHide() {
        if (null == aPlayer) return
        mVisible = false
        top_panel.visibility = View.GONE
        bottom_panel.visibility = View.GONE
        player_lock.visibility = View.GONE

    }

    override fun setVideoTile(name: String) {
        close_view!!.text = name
    }

    override fun userSeekPlayProgress(seekPostionMs: Int) {
        if (null == aPlayer) return
        val currentPlayPos = aPlayer!!.position
        val isChangeOverSeekGate = isOverSeekGate(seekPostionMs, currentPlayPos)
        if (!isChangeOverSeekGate) {
            return
        }
        if (mIsTouchingSeekbar) {
            stopUIUpdateThread()
        }
        //mIsTouchingSeekbar = true;
        controlViewShow()
        setTimeTextView(seekPostionMs, aPlayer!!.duration)
        aPlayer!!.position = seekPostionMs
    }

    override fun updateUIPlayStation(currentPlayTimeMs: Int, durationTimeMs: Int) {
        setTimeTextView(currentPlayTimeMs, durationTimeMs)
        if (durationTimeMs > 0 && currentPlayTimeMs >= 0) {
            play_seek_bar!!.max = durationTimeMs
            play_seek_bar.progress = currentPlayTimeMs
        } else {
            play_seek_bar!!.progress = 0
        }

    }

    private fun startUIUpdateThread() {
        if (null == mUpdateThread) {
            mIsNeedUpdateUIProgress = true
            mUpdateThread = Thread(UpdatePlayUIProcess(this, handle!!, aPlayer))
            mUpdateThread!!.start()
        }
    }

    private fun stopUIUpdateThread() {
        mIsNeedUpdateUIProgress = false
        mUpdateThread = null
    }

    private fun isOverSeekGate(seekBarPositionMs: Int, currentPlayPosMs: Int): Boolean {
        val SEEK_MIN_GATE_MS = 1000
        return Math.abs(currentPlayPosMs - seekBarPositionMs) > SEEK_MIN_GATE_MS
    }

    override fun ismIsNeedUpdateUIProgress(): Boolean {
        return mIsNeedUpdateUIProgress
    }

    override fun ismIsTouchingSeekbar(): Boolean {
        return mIsTouchingSeekbar
    }

    override fun setTimeTextView(currentPlayTimeMs: Int, durationTimeMs: Int) {
        var currSecond = currentPlayTimeMs / 1000
        currSecond = if (currSecond > 0) currSecond else 0
        var duraSecond = durationTimeMs / 1000
        duraSecond = if (duraSecond > 0) duraSecond else 0
        play_time!!.text = getString(R.string.play_time, TimeUtil.formatFromSecond(currSecond), TimeUtil.formatFromSecond(duraSecond))
        val batteryicon = SystemConfig.instance.batteryIcon
        battery_icon!!.setImageDrawable(resources.getDrawable(batteryicon))
        system_time!!.text = TimeUtil.getNowTime("HH:mm")
        playerPresenter!!.uaDataPlayerTime(currentPlayTimeMs, durationTimeMs)
    }

    override fun openVideo(path: String) {
        aPlayer!!.open(path)
    }

    override fun playPause() {
        if (null == aPlayer) {
            return
        }
        val state = aPlayer!!.state
        if (state == APlayerAndroid.PlayerState.APLAYER_PLAYING) {
            play_pause!!.setImageDrawable(resources.getDrawable(R.drawable.ic_video_player))
            aPlayer!!.pause()
        } else {
            play_pause!!.setImageDrawable(resources.getDrawable(R.drawable.ic_video_stop))
            aPlayer!!.play()
        }
    }

    fun playPauseClick(view: View) {
        playPause()
        controlViewShow()
    }

    fun moreViewClick(view: View) {
        if (null == aPlayer) {
            return
        }
        if (mPopupWindow == null) {
            mPopupWindow = PlayerPopupWindow(this, play_view_root!!, aPlayer!!)
        }
        mPopupWindow!!.showAtLocation(play_view_root, Gravity.END, 0, 0)
    }

    private var lock: Boolean = false

    fun toggleLockClick(view: View) {
        lock = !lock;
        if (lock) {
            player_lock.setImageResource(R.drawable.ic_locked)
            top_panel.visibility = View.GONE
            bottom_panel.visibility = View.GONE
        } else {
            player_lock.setImageResource(R.drawable.ic_unlock)
            top_panel.visibility = View.VISIBLE
            bottom_panel.visibility = View.VISIBLE
            hidePlayControl!!.resetHideTimer()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> if (null != aPlayer) {
                if (mPopupWindow == null) {
                    mPopupWindow = PlayerPopupWindow(this, play_view_root!!, aPlayer!!)
                }
                mPopupWindow!!.setSystemPlayAudio()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStop() {
        if (null != aPlayer) {
            aPlayer!!.play()
            playPause()
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (null != aPlayer) {
            aPlayer!!.close()
            aPlayer!!.destroy()
            destroyPopWind()
        }
        super.onDestroy()
    }
}
