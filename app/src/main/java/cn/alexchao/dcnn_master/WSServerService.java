package cn.alexchao.dcnn_master;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;

public class WSServerService extends Service {
    private static final String TAG = "WSService";
    private static final int ID = 120;
    private boolean mIsRunning = false;
    private WSServer mMaster;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsRunning) {
            Log.d(TAG, "Start Service ...");
            this.mIsRunning = true;
        } else {
            Log.d(TAG, "The Service is already running ...");
            return super.onStartCommand(intent, flags, startId);
        }

        // build notification
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .setContentTitle("WS Server")                // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher)          // 设置状态栏内的小图标
                .setContentText("WS Server is running")      // 设置上下文内容
                .setWhen(System.currentTimeMillis());        // 设置该通知发生的时间
        Notification notification = builder.build();         // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND;  // 设置为默认的声音

        startForeground(ID, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    // do not bind this service; use startService instead
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Start WS Service");
        preSettingOfWS();
        int port = 8888;
        try {
            mMaster = new WSServer(port);
            mMaster.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (mMaster != null) {
            mMaster = null;
            stopForeground(true);
            this.mIsRunning = false;
            Log.d(TAG, "Stop WS Service");
        }
    }

    private void preSettingOfWS() {
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
    }
}
