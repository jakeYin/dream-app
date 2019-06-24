package com.dream.tlj.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by admin on 2016/7/6.
 */
object TimeUtil {
    fun formatFromSecond(totalSecond: Int): String {
        val hour = totalSecond / 3600
        val min = totalSecond % 3600 / 60
        val second = totalSecond % 60

        var fortmatStr = ""
        if (hour > 0) {
            fortmatStr = String.format("%02d:%02d:%02d", hour, min, second)
        } else {
            fortmatStr = String.format("%02d:%02d", min, second)
        }

        return fortmatStr
    }

    fun secToTime(time: Int): String {
        var timeStr: String? = null
        var hour = 0
        var minute = 0
        var second = 0
        if (time <= 0)
            return "00:00"
        else {
            minute = time / 60
            if (minute < 60) {
                second = time % 60
                timeStr = unitFormat(minute) + ":" + unitFormat(second)
            } else {
                hour = minute / 60
                if (hour > 99)
                    return "00:00:00"
                minute = minute % 60
                second = time - hour * 3600 - minute * 60
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second)
            }
        }
        return timeStr
    }

    fun unitFormat(i: Int): String {
        var retStr: String? = null
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i)
        else
            retStr = "" + i
        return retStr
    }

    fun getNowTime(format: String): String {
        val simpleDateFormat = SimpleDateFormat(format)
        val date = Date(System.currentTimeMillis())
        return simpleDateFormat.format(date)
    }

}
