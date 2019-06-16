package cn.sddman.download.spider

import cn.sddman.download.cache.CacheHttpUtils
import cn.sddman.download.mvp.e.MagnetDetail
import cn.sddman.download.mvp.e.MagnetRule
import com.orhanobut.logger.Logger
import org.htmlcleaner.CleanerProperties
import org.htmlcleaner.DomSerializer
import org.htmlcleaner.HtmlCleaner
import org.jsoup.Jsoup
import org.w3c.dom.NodeList
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.regex.Pattern
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class MagnetFetchDyttDetailParserImp : MagnetFetchInf() {
    override fun parser(rule: MagnetRule, url:String): List<MagnetDetail> {
        val list = arrayListOf<MagnetDetail>()
        try {
            val html = CacheHttpUtils.get(url)
            val xPath = XPathFactory.newInstance().newXPath()
            val tagNode = HtmlCleaner().clean(html)
            val dom = DomSerializer(CleanerProperties()).createDOM(tagNode)
            val result = xPath.evaluate(rule.detailLinks, dom, XPathConstants.NODESET) as NodeList

            for (i in 0 until result.length) {
                val node = result.item(i)
                if (node != null) {
                    val downloadUrl = node.attributes.getNamedItem("href")?.textContent
                    downloadUrl?.let {
                        if (it.toLowerCase().startsWith("ftp://")){
                            list.add(MagnetDetail(it))
                        }
                        if (it.toLowerCase().startsWith("magnet:")){
                            list.add(MagnetDetail(it.substringBefore("&")))
                        }
                    }
                }
            }
        }catch (e:Exception){
            Logger.d("===fail===$url")
            e.printStackTrace()
        }

        return list
    }


    override fun transformPage(page: Int?): Int {
        return if (page == null || page <= 0) 1 else page+1
    }

    private fun transformUrl(url: String, keyword: String, page: Int?): String {
        try {
            return String.format(url, URLEncoder.encode(keyword, "gb2312"), page)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 磁力链转换
     *
     * @param url
     * @return
     */
    private fun transformMagnet(url: String): String {
        val regex = "magnet:?[^\"]+"
        val matches = Pattern.matches(regex, url)
        if (matches) {
            return url
        } else {
            var newMagnet: String
            try {
                val sb = StringBuffer(url)
                val htmlIndex = url.lastIndexOf(".html")
                if (htmlIndex != -1) {
                    sb.delete(htmlIndex, sb.length)
                }
                val paramIndex = url.indexOf("&")
                if (paramIndex != -1) {
                    sb.delete(paramIndex, sb.length)
                }
                val paramIndex2 = url.indexOf("?r")
                if (paramIndex2 != -1) {
                    sb.delete(paramIndex2, sb.length)
                }
                if (sb.length >= 40) {
                    newMagnet = sb.substring(sb.length - 40, sb.length)
                } else {
                    newMagnet = url
                }
            } catch (e: Exception) {
                e.printStackTrace()
                newMagnet = url
            }

            return String.format("magnet:?xt=urn:btih:%s", newMagnet)
        }
    }
}