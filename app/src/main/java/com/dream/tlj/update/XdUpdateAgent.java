package com.dream.tlj.update;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class XdUpdateAgent {

    protected XdUpdateAgent() {}

    protected static XdUpdateAgent sInstance;

    protected boolean mForceUpdate;
    protected XdUpdateBean mUpdateBeanProvided;
    protected String mJsonUrl;
    protected int mIconResId;
    protected boolean mShowDialogIfWifi;
    protected OnUpdateListener mListener;

    public void update(final Activity activity) {
        if (mUpdateBeanProvided == null && TextUtils.isEmpty(mJsonUrl)) {
            System.err.println("Please set updateBean or mJsonUrl.");
            mForceUpdate = false;
            return;
        }
        if (mUpdateBeanProvided != null) {
            mForceUpdate = mUpdateBeanProvided.forceUpdate;
            updateMatters(mUpdateBeanProvided, activity);
        } else {
            Flowable.create(new FlowableOnSubscribe<String>() {
                @Override
                public void subscribe(FlowableEmitter<String> e) throws Exception {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(mJsonUrl).build();
                    Response response;
                    try {
                        response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            e.onNext(response.body().string());
                        } else {
                            e.onError(new IOException(response.code() + ": " + response.body().string()));
                        }
                    } catch (Throwable t) {
                        e.onError(t);
                    }
                }
            }, BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String responseBody) throws Exception {
                            if (XdConstants.debugMode) System.out.println(responseBody);
                            final XdUpdateBean xdUpdateBean = new XdUpdateBean();
                            try {
                                JSONObject jsonObject = new JSONObject(responseBody);
                                xdUpdateBean.versionCode = jsonObject.getInt("versionCode");
                                xdUpdateBean.size = jsonObject.getInt("size");
                                xdUpdateBean.versionName = jsonObject.getString("versionName");
                                xdUpdateBean.url = jsonObject.getString("url");
                                xdUpdateBean.note = jsonObject.getString("note");
                                xdUpdateBean.md5 = jsonObject.getString("md5");
                            } catch (Exception e) {
                                e.printStackTrace();
                                mForceUpdate = false;
                                return;
                            }
                            updateMatters(xdUpdateBean, activity);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mForceUpdate = false;
                        }
                    });
        }
    }

    protected void updateMatters(final XdUpdateBean updateBean, final Activity activity) {
        final int currentCode = XdUpdateUtils.getVersionCode(activity.getApplicationContext());
        final int versionCode = updateBean.versionCode;
        final String versionName = updateBean.versionName;
        if (currentCode < versionCode) {
            if (mListener != null) mListener.onUpdate(true, updateBean);
            final SharedPreferences sp = activity.getSharedPreferences("update", Context.MODE_PRIVATE);
            long lastIgnoredDayBegin = sp.getLong("time", 0);
            int lastIgnoredCode = sp.getInt("versionCode", 0);
            long todayBegin = XdUpdateUtils.dayBegin(new Date()).getTime();
            if (!mForceUpdate && todayBegin == lastIgnoredDayBegin && versionCode == lastIgnoredCode) {
                mForceUpdate = false;
                return;
            }
            final File file = new File(activity.getExternalCacheDir(), "update.apk");
            if (file.exists()) {
                XdUpdateUtils.getMd5ByFile(file, new FlowableSubscriber<String>() {
                    boolean fileExists = false;
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }
                    public void onComplete() {
                    }
                    @Override
                    public void onError(Throwable e) {
                        file.delete();
                        e.printStackTrace();
                        proceedToUI(sp, file, fileExists, activity, versionName, updateBean, versionCode);
                    }

                    @Override
                    public void onNext(String md5JustDownloaded) {
                        String md5InUpdateBean = updateBean.md5;
                        if (md5JustDownloaded.equalsIgnoreCase(md5InUpdateBean)) {
                            fileExists = true;
                        } else {
                            file.delete();
                            System.err.println("MD5 mismatch. md5JustDownloaded: " + md5JustDownloaded + ". md5InUpdateBean: " + md5InUpdateBean + ".");
                        }
                        proceedToUI(sp, file, fileExists, activity, versionName, updateBean, versionCode);
                    }
                });
            } else {
                proceedToUI(sp, file, false, activity, versionName, updateBean, versionCode);
            }
        } else {
            if (mListener != null) mListener.onUpdate(false, updateBean);
        }
        mForceUpdate = false;
    }

    protected void proceedToUI(SharedPreferences sp, File file, boolean fileExists, Activity activity, String versionName, XdUpdateBean xdUpdateBean, int versionCode) {
        if (XdUpdateUtils.isWifi(activity.getApplicationContext())) {
            if (fileExists){
                showAlertDialog(sp, file, activity, versionName, xdUpdateBean, versionCode);
            } else {
                Intent intent = new Intent(activity, XdUpdateService.class);
                intent.putExtra("xdUpdateBean", xdUpdateBean);
                intent.putExtra("appIcon", mIconResId);
                activity.startService(intent);
            }
        }
    }


    protected void showAlertDialog(final SharedPreferences sp, final File file, final Activity activity, final String versionName, final XdUpdateBean xdUpdateBean, final int versionCode) {
            AlertDialog.Builder builder = new AlertDialog
                    .Builder(activity)
                    .setCancelable(false)
                    .setTitle(versionName + " " + XdConstants.hintText)
                    .setMessage(xdUpdateBean.note)
                    .setNegativeButton(XdConstants.laterText, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sp.edit()
                                    .putLong("time", XdUpdateUtils.dayBegin(new Date()).getTime())
                                    .putInt("versionCode", versionCode)
                                    .putString("versionName", versionName)
                                    .apply();
                        }
                    })
            .setPositiveButton(XdConstants.installText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                                .detectFileUriExposure()
                                .penaltyLog()
                                .build());
                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
            });
            try {builder.show();} catch (Exception ignored) {}
    }

    public static class Builder {

        protected XdUpdateBean updateBeanProvided;
        protected String jsonUrl;
        protected int iconResId;
        protected boolean showDialogIfWifi;
        protected OnUpdateListener l;

        public Builder setUpdateBean(XdUpdateBean updateBeanProvided) {
            this.updateBeanProvided = updateBeanProvided;
            return this;
        }

        public Builder setJsonUrl(String jsonUrl) {
            this.jsonUrl = jsonUrl;
            return this;
        }

        public Builder setIconResId(int iconResId) {
            this.iconResId = iconResId;
            return this;
        }

        public Builder setShowDialogIfWifi(boolean showDialogIfWifi) {
            this.showDialogIfWifi = showDialogIfWifi;
            return this;
        }

        public Builder setOnUpdateListener(OnUpdateListener l) {
            this.l = l;
            return this;
        }

        public Builder setDebugMode(boolean debugMode) {
            XdConstants.debugMode = debugMode;
            return this;
        }

        public Builder setDownloadText(String downloadText) {
            if (!TextUtils.isEmpty(downloadText)) XdConstants.downloadText = downloadText;
            return this;
        }

        public Builder setInstallText(String installText) {
            if (!TextUtils.isEmpty(installText)) XdConstants.installText = installText;
            return this;
        }

        public Builder setLaterText(String laterText) {
            if (!TextUtils.isEmpty(laterText)) XdConstants.laterText = laterText;
            return this;
        }

        public Builder setHintText(String hintText) {
            if (!TextUtils.isEmpty(hintText)) XdConstants.hintText = hintText;
            return this;
        }

        public Builder setDownloadingText(String downloadingText) {
            if (!TextUtils.isEmpty(downloadingText)) XdConstants.downloadingText = downloadingText;
            return this;
        }

        public XdUpdateAgent build() {
            if (sInstance == null) sInstance = new XdUpdateAgent();
            if (updateBeanProvided != null) {
                sInstance.mUpdateBeanProvided = updateBeanProvided;
            } else {
                sInstance.mJsonUrl = jsonUrl;
            }
            sInstance.mIconResId = iconResId;
            sInstance.mShowDialogIfWifi = showDialogIfWifi;
            sInstance.mListener = l;
            return sInstance;
        }
    }

    public interface OnUpdateListener {
        void onUpdate(boolean needUpdate, XdUpdateBean updateBean);
    }
}
