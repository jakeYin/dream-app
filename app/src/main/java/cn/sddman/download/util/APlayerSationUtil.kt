package cn.sddman.download.util

import com.aplayer.aplayerandroid.APlayerAndroid

object APlayerSationUtil {
    fun isStopByUserCall(playRet: String): Boolean {
        val stopCode = StringUtil.StringToLong(APlayerAndroid.PlayCompleteRet.PLAYRE_RESULT_CLOSE)
        val currentCode = StringUtil.StringToLong(playRet)

        return stopCode == currentCode
    }

}
