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

    ConnectionThread threat = null;
    AudioManager audioManager;
    ComponentName componentName;
    boolean locker = false;
    boolean shoutup = false;
    DevicePolicyManager devicePolicyManager;
    String code="";
    PowerManager powerManager;
    public Student_Service() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(),"开始了服务",Toast.LENGTH_SHORT).show();
        powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);



    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        code = intent.getStringExtra("clazz_code");
        Log.d(TAG, "onCreate: start service");
        HashMap<String,String> online = new HashMap<>();
        online.put("operation","Online");
        online.put("clazz_code",code);
        online.put("stu_id", Nowusers.getIdentitycode());
        JSONObject onlinejson = new JSONObject(online);
        if (Net.isNetworkAvailable(getApplicationContext())){
            threat =new  ConnectionThread(onlinejson.toString());
            threat.start();
        }
        else{
            Toast.makeText(getApplicationContext(),"您的网络似乎开小差了呢", Toast.LENGTH_SHORT).show();
        }




        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();  //获取消息中的Bundle对象
            String str = b.getString("data");//获取键为data的字符串的值
            JSONObject json = null;
            try {
                json = new JSONObject(str);
                println("收到信息 "+json);
                String opreation = (String) json.get("operation");
                switch (opreation){
                    case "Lock":
                        //TODO:手机锁屏
                        Lock_phone();
                        break;
                    case "Release":
                        Release();
                        //TODO:解锁手机
                        break;
                    case "ShoutUp":
                        Shout_Up();
                        //TODO：手机静音
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    };

    class ConnectionThread extends Thread{
        String message = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        public ConnectionThread(String msg){
            message = msg;
        }

        @Override
        public void run() {
            super.run();
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
            }
            try {
                println("尝试发送");
                new PrintWriter(new OutputStreamWriter(Pools.socket.getOutputStream(), "UTF-8"),
                        true
                ).println(message);
                BufferedReader br =new BufferedReader(new InputStreamReader(Pools.socket.getInputStream(), "UTF-8"));
                while (true) {
                    String  readline = br.readLine();
                    if (threat.isInterrupted()) break;
                    if (readline != null) {
                        Bundle b = new Bundle();
                        Message msg = new Message();
                        b.putString("data", readline);
                        msg.setData(b);
                        handler.sendMessage(msg);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void Lock_phone_WithTime(Long timeMs){
        if (isAdminActive()) {
            while(locker&& powerManager.isScreenOn())
            devicePolicyManager.setMaximumTimeToLock(componentName, timeMs);

        } else {

        }
    }

    private void Lock_phone(){
        if (isAdminActive()) {
            //true为打开，false为关闭
            while(locker&& powerManager.isScreenOn())
            devicePolicyManager.lockNow();
        } else {
            Toast.makeText(getApplicationContext(), "设备管理器未激活", Toast.LENGTH_SHORT).show();
        }

    }

    private void Shout_Up(){
        shoutup = true;
        while(shoutup){
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        }
    }


    private void Release(){
        locker=false;
        shoutup = false;
    }






    /**
     * 判断该组件是否有系统管理员的权限（【系统设置-安全-设备管理器】中是否激活）
     * @return
     */
    private boolean isAdminActive() {
        return devicePolicyManager.isAdminActive(componentName);
    }

}
