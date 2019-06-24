package com.dream.tlj.update;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class XdUpdateService extends Service {

    protected volatile int mFileLength;
    protected volatile int mLength;
    protected File mFile;
    protected Disposable mDisposable;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final XdUpdateBean xdUpdateBean = (XdUpdateBean) intent.getSerializableExtra("xdUpdateBean");
        if (xdUpdateBean == null) {
            stopSelf();
            return START_NOT_STICKY;
        }
        mDisposable = Flowable.create(new FlowableOnSubscribe<Response>() {
            @Override
            public void subscribe(FlowableEmitter<Response> e) throws Exception {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(xdUpdateBean.url).build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        e.onNext(response);
                    } else {
                        e.onError(new IOException(response.code() + ": " + response.body().string()));
                    }
                } catch (Throwable t) {
                    e.onError(t);
                }
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response response) {
                        Logger.d("start download new version");
                        InputStream is = null;
                        FileOutputStream fos = null;
                        try {
                            is = response.body().byteStream();
                            mFileLength = (int) response.body().contentLength();
                            mFile = new File(getExternalCacheDir(), "update.apk");
                            if (mFile.exists()) {
                                mFile.delete();
                            }
                            fos = new FileOutputStream(mFile);
                            byte[] buffer = new byte[8192];
                            int hasRead;
                            while ((hasRead = is.read(buffer)) >= 0) {
                                fos.write(buffer, 0, hasRead);
                                mLength = mLength + hasRead;
                            }
                            mLength = 0;
                            if (mFile.exists()) {
                                String md5JustDownloaded = XdUpdateUtils.getMd5ByFile(mFile);
                                String md5InUpdateBean = xdUpdateBean.md5;
                                if (!md5JustDownloaded.equalsIgnoreCase(md5InUpdateBean)) {
                                    throw new Exception("MD5 mismatch. md5JustDownloaded: " + md5JustDownloaded + ". md5InUpdateBean: " + md5InUpdateBean + ".");
                                }
                            }
                            Logger.d("end download new version");
                        } catch (Exception e) {
                            e.printStackTrace();
                            mFile.delete();
                        } finally {
                            XdUpdateUtils.closeQuietly(fos);
                            XdUpdateUtils.closeQuietly(is);
                            stopSelf();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) mDisposable.dispose();
    }
}
