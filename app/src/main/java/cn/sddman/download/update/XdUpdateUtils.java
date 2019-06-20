package cn.sddman.download.update;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.*;
import io.reactivex.schedulers.*;

public class XdUpdateUtils {

    protected XdUpdateUtils() {}

    public static void closeQuietly(Closeable c) {
        if (c == null) return;
        try {c.close();} catch (Throwable ignored) {}
    }

    public static Date dayBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static String formatToMegaBytes(long bytes) {
        double megaBytes = bytes / 1048576.0;
        if (megaBytes < 1) return new DecimalFormat("0.0").format(megaBytes);
        return new DecimalFormat("#.0").format(megaBytes);
    }

    public static void getMd5ByFile(final File file, FlowableSubscriber<String> md5Subscriber) {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    MappedByteBuffer byteBuffer = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                    MessageDigest md5 = MessageDigest.getInstance("MD5");
                    md5.update(byteBuffer);
                    BigInteger bi = new BigInteger(1, md5.digest());
                    e.onNext(String.format("%032x", bi));
                } catch (Throwable t) {
                    e.onError(t);
                } finally {
                    closeQuietly(fis);
                }
            }
        }, BackpressureStrategy.BUFFER)
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(md5Subscriber);
    }

    public static String getMd5ByFile(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            MappedByteBuffer byteBuffer = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            return String.format("%032x", bi);
        } catch (Exception e) {
            e.printStackTrace();
            return file.toString();
        } finally {
            closeQuietly(fis);
        }
    }

    public static String getApplicationName(Context app) {
        PackageManager pm;
        ApplicationInfo ai;
        try {
            pm = app.getPackageManager();
            ai = pm.getApplicationInfo(app.getPackageName(), 0);
            return pm.getApplicationLabel(ai).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return app.getPackageName();
        }
    }

    public static int getVersionCode(Context app) {
        PackageManager pm = app.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(app.getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static String getVersionName(Context app) {
        PackageManager pm = app.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(app.getPackageName(), 0);
            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return app.getPackageName();
        }
    }

    public static int getAppIconResId(Context app) {
        PackageManager pm = app.getPackageManager();
        String packageName = app.getPackageName();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai.icon;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return app.getResources().getIdentifier("sym_def_app_icon", "mipmap", "android");
            } catch (Exception e1) {
                e1.printStackTrace();
                return 0;
            }
        }
    }

    public static boolean isWifi(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET || networkInfo.getType() == 17 || networkInfo.getType() == -1 || networkInfo.getType() == 13 || networkInfo.getType() == 16);
    }
}
