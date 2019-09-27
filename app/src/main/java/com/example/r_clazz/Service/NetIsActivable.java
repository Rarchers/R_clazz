package com.example.r_clazz.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.r_clazz.NetWork.Net;
import com.example.r_clazz.NetWork.Pools;

import java.net.Socket;

public class NetIsActivable extends Service {
    public NetIsActivable() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        System.out.println("开始监测网络情况");
        if (!Net.isNetworkAvailable(this)){
            Pools.socket = null;
            System.out.println("网络不可用");

        }
        else{
            System.out.println("网络可用");
            try{
                Pools.socket = new Socket("119.23.225.4", 8000);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }
}
