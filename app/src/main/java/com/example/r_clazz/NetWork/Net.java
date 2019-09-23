package com.example.r_clazz.NetWork;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public  class Net {

    private  Socket socket = null;


    public  Net(){
        try{
            System.out.println("开始连结服务器");
            this.socket = new Socket("119.23.225.4",8000);
            System.out.println("连接服务器成功");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("连接服务器失败");
        }

    }

    public  void sendMessage(String message){
        try{
            new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true).println(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }










}
