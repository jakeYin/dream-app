package cn.sddman.download.cache

import android.os.AsyncTask
import android.util.LruCache
import cn.sddman.download.common.Const
import cn.sddman.download.util.AppSettingUtil
import cn.sddman.download.util.StringUtil
import com.jakewharton.disklrucache.DiskLruCache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xutils.common.util.MD5
import java.io.*
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset


object CacheHttpUtils {

    val maxMemory = Runtime.getRuntime().maxMemory().toInt()
    val cacheSize = maxMemory / 8
    val diskLruCache: DiskLruCache = DiskLruCache.open(File(AppSettingUtil.instance.fileCachePath), 1, 1, 100 * 1024 * 1024)
    val memoryCache: LruCache<String, String> = LruCache(cacheSize)

    fun get(url: String): String? {
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
            SaveCacheTask().executeOnExecutor(Const.THREAD_POOL_EXECUTOR, url)
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
            var response = responseBytes?.let { String(it, Charset.forName("GBK")) }
            if (response != null) {
                val key = md5(url)
                val editor = diskLruCache.edit(key)
                editor.set(0, response);
                editor.commit();
                memoryCache.put(key, response)
                return response
            } else {
                return ""
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return ""
    }

    fun getAsString(key: String): String? {
        var inputStream: InputStream? = null
        try {
            //write READ
            inputStream = diskLruCache.get(key).getInputStream(0)
            if (inputStream == null) return null
            val sb = StringBuilder()
            var len = 0
            val buf = ByteArray(128)
            len = inputStream!!.read(buf)
            while (len != -1) {
                sb.append(String(buf, 0, len))
                len = inputStream!!.read(buf)
            }
            return URLDecoder.decode(sb.toString(), "UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
            if (inputStream != null)
                try {
                    inputStream!!.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }

        }

        return null
    }

    fun put(key: String, value: String) {
        var value = value
        try {
            value = URLEncoder.encode(value, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        var edit: DiskLruCache.Editor? = null
        var bw: BufferedWriter? = null
        try {
            edit = diskLruCache.get(key).edit()
            if (edit == null) return
            val os = edit.newOutputStream(0)
            bw = BufferedWriter(OutputStreamWriter(os))
            bw!!.write(value)
            edit.commit()//write CLEAN
        } catch (e: IOException) {
            e.printStackTrace()
            try {
                //s
                edit!!.abort()//write REMOVE
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        } finally {
            try {
                if (bw != null)
                    bw!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun getAsStringNew(key: String): String {
        var buf: BufferedReader? = null
        val stringBuffer = StringBuffer()
        try {
            var line: String? = null
            buf = BufferedReader(InputStreamReader(diskLruCache.get(key).getInputStream(0)))
            line = buf!!.readLine()
            while (line!= null) {
                stringBuffer.append(line)
                line = buf!!.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                buf!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return stringBuffer.toString()
    }


    internal class SaveCacheTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String? {
            return getRemote(params[0])
        }
    }
}