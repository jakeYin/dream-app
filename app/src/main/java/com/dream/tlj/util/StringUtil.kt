package com.dream.tlj.util

import java.text.DecimalFormat

object StringUtil {
    fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.length == 0
    }


    fun stringToDouble(a: String): Double {
        var b = java.lang.Double.valueOf(a)
        val df = DecimalFormat("#.0")
        val temp = df.format(b)
        b = java.lang.Double.valueOf(temp)
        return b
    }

    fun StringToLong(str: String): Long {
        var str = str
        val lowerCase = str.toLowerCase()
        val HEX_PREF = "0x"

        var radix = 10     //默认十进制
        if (str.startsWith(HEX_PREF)) {
            str = str.substring(HEX_PREF.length)
            radix = 16
        }

        return java.lang.Long.parseLong(str, radix)
    }

    fun StringToInt(str: String): Int {
        val lval = StringToLong(str)
        return lval.toInt()
    }

    fun equalsIgnoreCase(str0: String?, str1: String?): Boolean {
        if (null == str0 && null == str1) {
            return true
        }

        return if (null == str0 && null != str1 || null != str0 && null == str1) {
            false
        } else str0!!.equals(str1!!, ignoreCase = true)
    }
}
