package cn.sddman.download.cache

import android.os.AsyncTask
import android.util.LruCache
import cn.sddman.download.common.Const
import cn.sddman.download.util.AppSettingUtil
import cn.sddman.download.util.StringUtil
import com.jakewharton.disklrucache.DiskLruCache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.xutils.common.util.MD5
import java.io.*
import java.nio.charset.Charset


object CacheHttpUtils {

    val maxMemory = Runtime.getRuntime().maxMemory().toInt()
    val cacheSize = maxMemory / 8
    val diskLruCache: DiskLruCache = DiskLruCache.open(File(AppSettingUtil.instance.fileCachePath), 1, 1, 100 * 1024 * 1024)
    val memoryCache: LruCache<String, String> = LruCache(cacheSize)

    fun get(url: String): String {
        val key = md5(url)
        var result = memoryCache.get(key)
        if (StringUtil.isEmpty(result)) {
            result = diskLruCache.get(key)?.getString(0)
            result?.let{
                memoryCache.put(key,it)
            }
        }
        if (StringUtil.isEmpty(result)) {
            result = getRemote(url)
        } else {
            SaveCacheGetRemoteTask().executeOnExecutor(Const.THREAD_POOL_EXECUTOR, url)
        }
        return result
    }

    fun search(url:String,keyword:String):String{
        val key = md5(url+keyword)
        var result = memoryCache.get(key)
        if (StringUtil.isEmpty(result)) {
            result = diskLruCache.get(key)?.getString(0)
            result?.let{
                memoryCache.put(key,it)
            }
        }
        if (StringUtil.isEmpty(result)) {
            result = postRemote(url,keyword)
        } else {
            SaveCachePostRemoteTask().executeOnExecutor(Const.THREAD_POOL_EXECUTOR, url,keyword)
        }
        return result
    }

    private fun md5(key: String): String {
        return MD5.md5(key)
    }

    private fun getRemote(url: String): String {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                    .url(url)
                    .build()
            val call = client.newCall(request)
            var responseBytes = call.execute().body()?.bytes()
            var result = responseBytes?.let { String(it, Charset.forName("GBK")) }
            if (result != null) {
                val key = md5(url)
                val editor = diskLruCache.edit(key)
                editor.set(0, result);
                editor.commit();
                memoryCache.put(key, result)
                return result
            } else {
                return ""
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return ""
    }

    private fun postRemote(url: String, keyword: String): String {
        try {
            val client = OkHttpClient()
            var body = RequestBody.create(null, keyword)
            val request = Request.Builder().url(url)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(body)
                    .build()
            val call = client.newCall(request)
            var response = call.execute()
            println("=========="+response.toString())
            var responseBytes = response.body()?.bytes()
            var result = responseBytes?.let { String(it, Charset.forName("GBK")) }
            if (result != null) {
                val key = md5(url)
                val editor = diskLruCache.edit(key)
                editor.set(0, result);
                editor.commit();
                memoryCache.put(key, result)
                return result
            } else {
                return ""
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return ""
    }





    internal class SaveCacheGetRemoteTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String? {
            return getRemote(params[0])
        }
    }
    internal class SaveCachePostRemoteTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String? {
            return postRemote(params[0],params[1])
        }
    }
}