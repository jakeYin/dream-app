package cn.sddman.download.update;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;

public class UpdateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initUpdate();
        initJson();
    }

    private void initJson(){
        String s = "{\n" +
                "action:\"update_hot_words\",\n" +
                "hotwordList:[{title:\"123\"},{title:\"1234\"},{title:\"1234\"},{title:\"1234\"},{title:\"1234\"},{title:\"1234\"}],\n" +
                "updateBean:{\n" +
                "    \"versionCode\":401,\n" +
                "    \"versionName\":\"7.12\",\n" +
                "    \"url\":\"https://cdn2.jianshu.io/assets/web/nav-logo-4c7bbafe27adc892f3046e6978459bac.png\",\n" +
                "    \"note\":\"增加对电视剧的支持\",\n" +
                "    \"md5\":\"4c7bbafe27adc892f3046e6978459bac\",\n" +
                "    \"size\":17962350\n" +
                "    }\n" +
                "}";

        Gson gson = new Gson();
    }

    /**
     * {
     * "versionCode":4,                          //新版本的versionCode,int型
     * "versionName":"1.12",                     //新版本的versionName,String型
     * "url":"http://contoso.com/app.apk",       //APK下载地址,String型
     * "note":"Bug修复",                         //更新内容,String型
     * "md5":"D23788B6A1F95C8B6F7E442D6CA7536C", //32位MD5值,String型
     * "size":17962350                           //大小(字节),int型
     * }
     */

    private void initUpdate() {
        XdUpdateBean xdUpdateBean = new XdUpdateBean();
        xdUpdateBean.versionCode = 40;
        xdUpdateBean.versionName = "7.2";
        xdUpdateBean.url = "https://cdn2.jianshu.io/assets/web/nav-logo-4c7bbafe27adc892f3046e6978459bac.png";
        xdUpdateBean.md5 = "4c7bbafe27adc892f3046e6978459bac";
        xdUpdateBean.note = "jj修复bug";
        xdUpdateBean.size = 2693777;
        xdUpdateBean.forceUpdate = true;
        XdUpdateAgent updateAgent = new XdUpdateAgent
                .Builder()
                .setDebugMode(false)    //是否显示调试信息(可选,默认:false)
                .setUpdateBean(xdUpdateBean)    //设置通过其他途径得到的XdUpdateBean(2选1)
                .setShowDialogIfWifi(true)    //设置在WiFi下直接弹出AlertDialog而不使用Notification(可选,默认:false)
                .setOnUpdateListener(new XdUpdateAgent.OnUpdateListener() {
                    @Override
                    public void onUpdate(boolean needUpdate, XdUpdateBean updateBean) {
                        if (!needUpdate)
                            Toast.makeText(UpdateActivity.this, "您的应用为最新版本", Toast.LENGTH_SHORT).show();

                    }
                })
                .setDownloadText("立即下载")    //可选,默认为左侧所示的文本
                .setInstallText("立即安装(已下载)")
                .setLaterText("以后再说")
                .setHintText("版本更新")
                .setDownloadingText("正在下载")
                .build();
        updateAgent.update(this);
    }
}
