package com.dream.tlj.rule

import android.content.Context
import com.dream.tlj.App
import com.dream.tlj.util.GsonUtil

object Rule{
    var sourceRule:List<com.dream.tlj.mvp.e.MagnetRule>? = null
    var searchRule:List<com.dream.tlj.mvp.e.MagnetRule>? = null

    init {
        sourceRule = loadRules(App.instance!!.applicationContext, "source.json")
        searchRule = loadRules(App.instance!!.applicationContext, "search.json")
    }

    fun getRuleById(id: String?): com.dream.tlj.mvp.e.MagnetRule? {
        sourceRule?.let {
            for (x in it){
                if (x.id == id){
                    return x;
                }
            }
        }
        searchRule?.let {
            for (x in it){
                if (x.id == id){
                    return x
                }
            }
        }
        return null
    }

    private fun loadRules(context:Context, path:String): List<com.dream.tlj.mvp.e.MagnetRule>? {
        return GsonUtil.getRule(context, path)
    }
}
