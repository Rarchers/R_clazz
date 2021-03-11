package com.example.r_class.service

import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import android.widget.Toast
import com.example.r_class.model.receiver.AdminReceiver
import com.example.r_class.net.Pools
import com.example.r_class.utils.SPUtils
import org.json.JSONObject
import java.io.*
import java.net.Socket
import java.sql.DriverManager
import java.util.*

class Student_Service : Service() {


    var audioManager: AudioManager? = null
    var componentName: ComponentName? = null
    var locker = false
    var shoutup = false
    var devicePolicyManager: DevicePolicyManager? = null
    var code: String? = ""
    var powerManager: PowerManager? = null
    var dis: DataInputStream? = null
    var dos: DataOutputStream? = null
    var mWakeLock // 电源锁
            : WakeLock? = null

    var threadLock = false
    lateinit var spUtils: SPUtils
    fun Student_Service() {}

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(applicationContext, "开始了服务", Toast.LENGTH_SHORT).show()
        powerManager = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, AdminReceiver::class.java)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        spUtils = SPUtils(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(ContentValues.TAG, "onDestroy: 服务被销毁了")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        threadLock = true
        code = intent.getStringExtra("clazz_code")
        Log.d(ContentValues.TAG, "onCreate: start service$code")
        val online = HashMap<String?, String?>()
        online["operation"] = "Online"
        online["clazz_code"] = code
        online["stu_id"] = spUtils.userName
        val onlinejson = JSONObject(online as Map<*, *>)
        Thread {
            while (Pools.socket == null) {
                try {
                    DriverManager.println("开始链接")
                    Pools.socket = Socket("119.23.225.4", 8000)
                    dis = DataInputStream(Pools.socket!!.getInputStream())
                    dos = DataOutputStream(Pools.socket!!.getOutputStream())
                    DriverManager.println("连接成功")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            try {
//                   new PrintWriter(new OutputStreamWriter(Pools.socket.getOutputStream(), "UTF-8"),
//                           true
//                   ).println(onlinejson);
                val br = BufferedReader(
                    InputStreamReader(
                        Pools.socket!!.getInputStream(),
                        "UTF-8"
                    )
                )
                var readline: String? = null
                while (true) {
                    if (br.readLine().also { readline = it } != null) {
                        var json: JSONObject? = null
                        json = JSONObject(readline)
                        DriverManager.println("收到信息 $json")
                        val opreation = json["operation"] as String
                        val codes = json.getString("Clazz_code") as String
                        if (codes == code) {
                            when (opreation) {
                                "Lock" -> {
                                    //TODO:手机锁屏
                                    println("当前操作，锁屏")
                                    locker = true
                                    Thread { Lock_phone() }.start()
                                }
                                "Release" -> {
                                    println("当前操作，解锁")
                                    Release()
                                }
                                "ShoutUp" -> {
                                    println("当前操作，静音")
                                    Shout_Up()
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }


    private fun Lock_phone_WithTime(timeMs: Long) {
        if (isAdminActive()) {
            while (locker && powerManager!!.isScreenOn) devicePolicyManager!!.setMaximumTimeToLock(
                componentName!!, timeMs
            )
        }
    }

    private fun Lock_phone() {
        if (isAdminActive()) {
            //true为打开，false为关闭
            println("当前操作，锁屏")
            while (locker) {
                println(locker)
                if (powerManager!!.isScreenOn) devicePolicyManager!!.lockNow()
            }
        } else {
            Toast.makeText(applicationContext, "设备管理器未激活", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Shout_Up() {
        audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
    }


    private fun Release() {
        println("当前操作，解锁")
        locker = false
        shoutup = false
    }


    /**
     * 判断该组件是否有系统管理员的权限（【系统设置-安全-设备管理器】中是否激活）
     *
     * @return
     */
    private fun isAdminActive(): Boolean {
        return devicePolicyManager!!.isAdminActive(componentName!!)
    }

    fun stop() {
        println("关闭线程")
        if (threadLock) {
            threadLock = false
        }
    }
}