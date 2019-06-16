package cn.sddman.download.rule

import android.content.Context
import cn.sddman.download.App
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.util.GsonUtil

object Rule{
    var sourceRule:List<MagnetRule>? = null
    var searchRule:List<MagnetRule>? = null

    init {
        sourceRule = loadRules(App.instance!!.applicationContext,"source.json")
        searchRule = loadRules(App.instance!!.applicationContext,"search.json")
    }

    fun getRuleById(id: String?): MagnetRule? {
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

    private fun loadRules(context:Context, path:String): List<MagnetRule>? {
        return GsonUtil.getRule(context, path)
    }
}
