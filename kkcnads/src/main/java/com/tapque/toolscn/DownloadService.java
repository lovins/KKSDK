package com.tapque.toolscn;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.tapque.kkcnads.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadService extends IntentService {

    private LocalBroadcastManager mLocalBroadcastManager;
    private NotificationManager nm;
    private NotificationCompat.Builder builder;
    private Notification nf;
    private Notification.Builder builder0;
    public static final String BROADCAST = "DOWNLOAD_BROADCAST";
    String fileName = "";
    private int channelId = 2;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("id","name", NotificationManager.IMPORTANCE_LOW);

            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(channel);
            nf = new Notification.Builder(this,"id").build();
            builder0 = new Notification.Builder(this,"id");
            startForeground(channelId,nf);
        } else {
            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //获取下载地址
        String apkUrl = intent.getDataString();
        String[] strs = apkUrl.split("/");
        if(null != strs && strs.length > 0){
            fileName = strs[strs.length - 1];
        } else {
            //将id放进Intent
            Intent localIntent = new Intent(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
            localIntent.putExtra("action","failed");
            mLocalBroadcastManager.sendBroadcast(localIntent);
            return;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            builder= new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentInfo("下载中...")
                    .setContentTitle("正在下载：" + fileName);
            nf=builder.build();
            nf.flags = Notification.FLAG_NO_CLEAR;
        } else {
            builder0.setContentTitle("正在下载：" + fileName)
                    .setSmallIcon(R.drawable.ic_launcher);
        }

        HttpURLConnection con=null;
        InputStream is=null;
        OutputStream os=null;
        try {
            URL url=new URL(apkUrl);
            con= (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5*1000);  //设置超时时间
            if(con.getResponseCode()==200){ //判断是否连接成功
                int fileLength = con.getContentLength();
                is=con.getInputStream();    //获取输入
                os = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName);
                byte[] buffer=new byte[1024*1024*10];
                long total=0;
                int count;
                int pro1=0;
                int pro2=0;
                while ((count=is.read(buffer))!=-1){
                    total+=count;
                    if (fileLength > 0)
                        pro1=(int) (total * 100 / fileLength);  //传递进度（注意顺序）
                    if(pro1!=pro2)
                        publishProgress(pro2=pro1);
                    os.write(buffer,0,count);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            failed();
            e.printStackTrace();
        } finally {
            try {
                if(is!=null){
                    is.close();
                }
                if(os!=null){
                    os.close();
                }
            } catch (IOException e) {
                failed();
                e.printStackTrace();
            }
            if(con!=null){
                con.disconnect();
            }
        }
    }

    private void publishProgress(int values) {
        Intent localIntent = new Intent(BROADCAST);
        String action = "";
        if (values != 100) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder0.setProgress(100, values, false);
                builder0.setContentText("下载"+values+"%");
                nf = builder0.build();
            } else {
                builder.setProgress(100, values, false);
                builder.setContentText("下载"+values+"%");
                nf = builder.build();
            }
            nm.notify(channelId, nf);
            action = "running";
        } else if (values == 100) {
            action = "success";
            localIntent.putExtra("fileName",fileName);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                builder0.setProgress(100, values, false);
                builder0.setContentTitle("下载完成")
                        .setContentText("");
                nf = builder0.build();
            }else {
                builder.setProgress(100, values, false);
                builder.setContentTitle("下载完成")
                        .setContentText("");
                nf = builder.build();
            }
            nm.notify(channelId, nf);
        }
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        localIntent.putExtra("action", action);
        mLocalBroadcastManager.sendBroadcast(localIntent);
    }

    private void failed(){
        Intent localIntent = new Intent(BROADCAST);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        localIntent.putExtra("action", "fail");
        mLocalBroadcastManager.sendBroadcast(localIntent);
    }

}
