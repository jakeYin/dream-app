package cn.sddman.download;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.sddman.download.cache.LocalCookieJar;
import cn.sddman.download.cache.RedirectInterceptor;
import cn.sddman.download.spider.MagnetFetchDyttDetailParserImp;
import cn.sddman.download.mvp.e.MagnetRule;
import cn.sddman.download.spider.MagnetFetchDyttImp;
import cn.sddman.download.spider.MagnetFetchInf;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test2() {
        try {
            String ruleStr = "{\"site\": \"电影天堂-Movie\",\n" +
                    "    \"url\": \"http://www.ygdy8.net\",\n" +
                    "    \"parserClass\": \"cn.sddman.download.spider.MagnetFetchDyttImp\",\n" +
                    "    \"parserDetailClass\":\"cn.sddman.download.spider.MagnetFetchDyttDetailParserImp\",\n" +
                    "    \"list\": \"//*[@id='header']/div/div[3]/div[3]/div[2]/div[2]/div[2]/ul/table\",\n" +
                    "    \"href\": \".//tbody/tr[2]/td[2]/b/a//@href\",\n" +
                    "    \"name\": \".//tbody/tr[1]/td[2]/b/a\",\n" +
                    "    \"desc\": \".//tbody/tr[2]\",\n" +
                    "    \"detailBaseUrl\":\"http://www.ygdy8.com\",\n" +
                    "    \"detailUrl\": \".//tbody/tr[2]\",\n" +
                    "    \"detailFtpLinks\":\"//*[@id='Zoom']//a\",\n" +
                    "    \"detailMagnetLinks\":\"//*[@id='Zoom']/span/p[1]/a\",\n" +
                    "    \"source\": \"http://s.ygdy8.com/plus/so.php?keyword=%1$s&searchtype=titlekeyword&channeltype=0&orderby=&kwtype=0&pagesize=10&typeid=1&PageNo=%2$s\"}";
            Gson gson = new Gson();
//*[@id="Zoom"]/span/table[2]/tbody/tr/td/a

            MagnetRule rule = gson.fromJson(ruleStr, MagnetRule.class);
            MagnetFetchDyttDetailParserImp imp = new MagnetFetchDyttDetailParserImp();
            imp.parser(rule, "https://www.ygdy8.com/html/gndy/dyzz/20190308/58314.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() {
        try {
            String ruleStr = "{\"site\": \"国内电影\",\n" +
                    "    \"url\": \"http://www.ygdy8.com\",\n" +
                    "    \"parserClass\": \"cn.sddman.download.spider.MagnetFetchDyttImp\",\n" +
                    "    \"parserDetailClass\":\"cn.sddman.download.spider.MagnetFetchDyttDetailParserImp\",\n" +
                    "    \"list\": \"//*[@id='header']/div/div[3]/div[3]/div[2]/div[2]/div[2]/ul/table\",\n" +
                    "    \"href\": \".//tbody/tr[2]/td[2]/b/a//@href\",\n" +
                    "    \"name\": \".//tbody/tr[2]/td[2]/b/a\",\n" +
                    "    \"desc\": \".//tbody/tr[4]/td\",\n" +
                    "    \"detailBaseUrl\":\"https://www.ygdy8.com/\",\n" +
                    "    \"detailUrl\": \".//tbody/tr[4]/td\",\n" +
                    "    \"detailLinks\":\"//*[@id='Zoom']//a\",\n" +
                    "    \"source\": \"https://www.ygdy8.com/html/gndy/china/list_4_%2$s.html?t=%1$s\"}";
            Gson gson = new Gson();
//*[@id="Zoom"]/span/table[2]/tbody/tr/td/a
            MagnetRule rule = gson.fromJson(ruleStr, MagnetRule.class);
            MagnetFetchInf fetchInf = (MagnetFetchDyttImp) Class.forName(rule.getParserClass()).newInstance();
            fetchInf.parser(rule, "", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tests4() {
//        final OkHttpClient client = new OkHttpClient().newBuilder()
//                .followRedirects(false)
//                .followSslRedirects(false)
//                .cookieJar(new LocalCookieJar())
//                .build();

        //使用
        OkHttpClient client = new OkHttpClient.Builder()
//                .followRedirects(false)
//                .followSslRedirects(false)//禁制OkHttp的重定向操作，我们自己处理重定向
//                .cookieJar(new LocalCookieJar())
//                .addInterceptor(new RedirectInterceptor())
                .build();

//        show:title
//tempid=1:
//keyboard=aaa:
//Submit:%C1%A2%BC%B4%CB%D1%CB%F7
        try {
            String keyword = URLEncoder.encode("行尸走肉","gb2312");

        String content = "show=title&tempid=1&keyboard=" +
                keyword +
                "&Submit=%C1%A2%BC%B4%CB%D1%CB%F7";
        RequestBody body = null;
        body = RequestBody.create(null,content);
        Request request = new Request.Builder().url("https://www.dygod.net/e/search/index.php")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(body)
                .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}