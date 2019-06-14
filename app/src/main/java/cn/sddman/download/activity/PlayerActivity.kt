package cn.sddman.download.activity

import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.SeekBar
import cn.sddman.download.R
import cn.sddman.download.common.BaseActivity
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.p.PlayerPresenter
import cn.sddman.download.mvp.p.PlayerPresenterImp
import cn.sddman.download.mvp.v.PlayerView
import cn.sddman.download.thread.HidePlayControl
import cn.sddman.download.thread.UpdataUIPlayHandler
import cn.sddman.download.thread.UpdatePlayUIProcess
import cn.sddman.download.util.*
import cn.sddman.download.view.PlayerPopupWindow
import com.aplayer.aplayerandroid.APlayerAndroid
import kotlinx.android.synthetic.main.activity_player.*


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
    private var downX: Float = 0.toFloat()
    private var downY: Float = 0.toFloat()


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

    private fun registerListener() {
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
                    downX = motionEvent.x
                    downY = motionEvent.y
                    Handler().postDelayed({
                        when (view.id) {
                            R.id.play_view_root -> controlViewToggle()
                            R.id.top_panel, R.id.bottom_panel -> controlViewShow()
                        }
                        destroyPopWind()
                    }, 100)
                }
                MotionEvent.ACTION_MOVE -> {
                    mIsTouchingSeekbar = true
                    val disX = motionEvent.x - downX
                    val disY = motionEvent.y - downY
                    if (Math.abs(disX) > Math.abs(disY)) {
                        // x control
                        if (Math.abs(disX) > 10){
                            val fastLength = 10 * disX
                            val newProgress = (aPlayer!!.position + fastLength);
                            userSeekPlayProgress(newProgress.toInt())
                            hidePlayControl!!.resetHideTimer()
                        }

                    }
//                    else {
//                        // y control
//                        if (Math.abs(disY) > 80){
//                            if (downX > SCREEN_WIDTH/2){
//                                //volume
//                                if (disY>0){
//                                    SystemConfig.instance.streamVolume--
//                                } else {
//                                    SystemConfig.instance.streamVolume++
//                                }
//                            }
////                            else {
////                                //brightness
////                                var old = SystemConfig.instance.screenBrightness
////                                if (disY>0){
////                                    old+=15
////                                } else {
////                                    old-=15
////                                }
////                                println("================old = $old")
////                                SystemConfig.instance.setWindowBrightness(this,old)
////                            }
//                        }
//                    }


                }
                MotionEvent.ACTION_UP -> {
                    mIsTouchingSeekbar = false
                    startUIUpdateThread()
                }
            }
            true
        }
        play_view_root!!.setOnTouchListener(playViewClick)
        top_panel!!.setOnTouchListener(playViewClick)
        bottom_panel!!.setOnTouchListener(playViewClick)
        //fullscreen_content.setOnTouchListener(playViewClick);
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
        top_panel!!.visibility = View.VISIBLE
        bottom_panel!!.visibility = View.VISIBLE
        hidePlayControl!!.resetHideTimer()
    }

    override fun controlViewHide() {
        if (null == aPlayer) return
        if (!mIsTouchingSeekbar) {
            mVisible = false
            top_panel!!.visibility = View.GONE
            bottom_panel!!.visibility = View.GONE
        }
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
