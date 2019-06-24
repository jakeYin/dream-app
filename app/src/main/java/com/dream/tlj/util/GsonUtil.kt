package com.dream.tlj.util

import android.content.Context
import com.dream.tlj.mvp.e.MagnetRule
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.util.*

/**
 * author  dengyuhan
 * created 2018/3/7 14:00
 */
object GsonUtil {
    private val gson = Gson()

    fun getMagnetRule(context: Context, path: String): Map<String, com.dream.tlj.mvp.e.MagnetRule> {
        val magnetRuleMap = LinkedHashMap<String, com.dream.tlj.mvp.e.MagnetRule>()
        val rules = getRule(context, path)
        for (rule in rules!!) {
            magnetRuleMap[rule.site!!] = rule
        }
        return magnetRuleMap
    }

    fun getMagnetRule(rules: List<com.dream.tlj.mvp.e.MagnetRule>): Map<String, com.dream.tlj.mvp.e.MagnetRule> {
        val magnetRuleMap = LinkedHashMap<String, com.dream.tlj.mvp.e.MagnetRule>()
        for (rule in rules) {
            magnetRuleMap[rule.site!!] = rule
        }
        return magnetRuleMap
    }

    fun getRule(context: Context, path: String): List<com.dream.tlj.mvp.e.MagnetRule>? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.assets.open(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return fromJson(inputStream, object : TypeToken<List<MagnetRule>>() {

        })
    }

    fun getRule(jsonText: String): List<com.dream.tlj.mvp.e.MagnetRule>? {
        return fromJson(jsonText, object : TypeToken<List<MagnetRule>>() {

        })
    }

    fun getJson(jsonText: String): JsonObject {
        return JsonParser().parse(jsonText).asJsonObject
    }

    fun <T> fromJson(inputStream: InputStream?, token: TypeToken<T>): T? {
        try {
            return gson.fromJson<T>(InputStreamReader(inputStream!!, "UTF-8"), token.type)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return null
    }

    fun <T> fromJson(jsonText: String, token: TypeToken<T>): T? {
        try {
            return gson.fromJson<T>(jsonText, token.type)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
