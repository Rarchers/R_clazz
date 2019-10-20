package com.example.r_clazz.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.r_clazz.DB.Nowusers;
import com.example.r_clazz.NetWork.Net;
import com.example.r_clazz.NetWork.Pools;
import com.example.r_clazz.Receiver.AdminReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static java.sql.DriverManager.println;

public class Student_Service extends Service {

    AudioManager audioManager;
    ComponentName componentName;
    boolean locker = false;
    boolean shoutup = false;
    DevicePolicyManager devicePolicyManager;
    String code = "";
    PowerManager powerManager;
    DataInputStream dis = null;
    DataOutputStream dos = null;
    PowerManager.WakeLock mWakeLock;// 电源锁

   static  boolean threadLock;
    public Student_Service() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getApplicationContext(), "开始了服务", Toast.LENGTH_SHORT).show();
        powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: 服务被销毁了");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        threadLock = true;
        code = intent.getStringExtra("clazz_code");
        Log.d(TAG, "onCreate: start service"+code);
        HashMap<String, String> online = new HashMap<>();
        online.put("operation", "Online");
        online.put("clazz_code", code);
        online.put("stu_id", Nowusers.getIdentitycode());
        final JSONObject onlinejson = new JSONObject(online);


       new Thread(new Runnable() {
           @Override
           public void run() {

                   while (Pools.socket == null) {
                       try {
                           println("开始链接");
                           Pools.socket = new Socket("119.23.225.4", 8000);
                           dis = new DataInputStream(Pools.socket.getInputStream());
                           dos = new DataOutputStream(Pools.socket.getOutputStream());
                           println("连接成功");

                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       if (!threadLock)break;
                   }
                   try {
//                   new PrintWriter(new OutputStreamWriter(Pools.socket.getOutputStream(), "UTF-8"),
//                           true
//                   ).println(onlinejson);
                       BufferedReader br = new BufferedReader(new InputStreamReader(Pools.socket.getInputStream(), "UTF-8"));
                       String readline = null;
                       while (true){
                           if ((readline = br.readLine()) != null) {
                               JSONObject json = null;
                               json = new JSONObject(readline);
                               println("收到信息 " + json);
                               String opreation = (String) json.get("operation");
                               String codes = (String) json.getString("Clazz_code");
                               if (codes.equals(code)){
                                   switch (opreation) {
                                       case "Lock":
                                           //TODO:手机锁屏
                                           System.out.println("当前操作，锁屏");
                                           locker = true;
                                           Lock_phone();
                                           break;
                                       case "Release":
                                           System.out.println("当前操作，解锁");
                                           Release();
                                           //TODO:解锁手机
                                           break;
                                       case "ShoutUp":
                                           System.out.println("当前操作，静音");
                                           Shout_Up();
                                           //TODO：手机静音
                                           break;
                                   }
                               }

                           }
                           if (!threadLock)break;
                       }

                   } catch (Exception e) {
                       e.printStackTrace();
                   }

           }
       }).start();

        return super.onStartCommand(intent,flags,startId);
    }



    private void Lock_phone_WithTime(Long timeMs) {
        if (isAdminActive()) {
            while (locker && powerManager.isScreenOn())
                devicePolicyManager.setMaximumTimeToLock(componentName, timeMs);

        } else {

        }
    }

    private void Lock_phone() {
        if (isAdminActive()) {
            //true为打开，false为关闭
            System.out.println("当前操作，锁屏");
            while (locker){
                System.out.println(locker);
                if (powerManager.isScreenOn())
                    devicePolicyManager.lockNow();
            }

        } else {
            Toast.makeText(getApplicationContext(), "设备管理器未激活", Toast.LENGTH_SHORT).show();
        }

    }

    private void Shout_Up() {

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

    }


    private void Release() {
        System.out.println("当前操作，解锁");
        locker = false;
        shoutup = false;
    }


    /**
     * 判断该组件是否有系统管理员的权限（【系统设置-安全-设备管理器】中是否激活）
     *
     * @return
     */
    private boolean isAdminActive() {
        return devicePolicyManager.isAdminActive(componentName);
    }

    public static void stop() {
        System.out.println("关闭线程");
        if (threadLock) {
            threadLock = false;
        }
    }
}
