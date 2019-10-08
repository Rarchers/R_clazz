package com.example.r_clazz.UI

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.r_clazz.R
import com.example.r_clazz.Utils.VerificationCodeView
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import com.example.r_clazz.Adapters.Course_Adapter
import com.example.r_clazz.Been.Course_Been
import com.example.r_clazz.DB.Nowusers
import com.example.r_clazz.Fragments.Classes
import com.example.r_clazz.NetWork.Net
import com.example.r_clazz.NetWork.Pools
import kotlinx.android.synthetic.main.dialog_clazz.*
import kotlinx.android.synthetic.main.fragment_classes__teacher.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.Socket


class JoinClazz : AppCompatActivity(), VerificationCodeView.OnCodeFinishListener {

    var threat:ConnectionThread? = null
    private var verificationcodeview: VerificationCodeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_clazz)
        verificationcodeview = findViewById(R.id.verificationcodeview)
        verificationcodeview?.onCodeFinishListener = this


    }


    override fun onTextChange(view: View?, content: String?) {
        if (view == verificationcodeview) {
            Toast.makeText(this, "输入中：" + content, Toast.LENGTH_SHORT).show()

        }
    }

    override fun onComplete(view: View?, content: String?) {
        if (view == verificationcodeview) {
            Toast.makeText(this, "输入完成：" + content, Toast.LENGTH_SHORT).show()
            if (Net.isNetworkAvailable(this)){
                var query_map = HashMap<String, String>()
                query_map.put("operation", "JoinClazz")
                query_map.put("clazz_code", content.toString())
                query_map.put("stucode",Nowusers.getIdentitycode())
                val query_json = JSONObject(query_map as Map<*, *>)
                print(query_json)
                threat = ConnectionThread(query_json.toString())
                threat?.start()
            }
            else{
                Toast.makeText(this,"您的网络似乎开小差了呢",Toast.LENGTH_SHORT).show()
            }
        }
    }

    var handler = @SuppressLint("HandlerLeak")
    object : Handler() {     //此处的object 要加，否则无法重写 handlerMessage
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val b = msg?.data  //获取消息中的Bundle对象
            val str = b?.getString("data")//获取键为data的字符串的值
            val json = JSONObject(str)
            println("收到信息+ $json")
            val opreation = json.get("operation")
            if (opreation == "ResponseJoin"){
                val success = json.getString("success")
                if (success=="true"){
                    Toast.makeText(this@JoinClazz,"加入成功，请返回后重新刷新页面", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@JoinClazz,"请检查您的加课码是否正确", Toast.LENGTH_SHORT).show()
                }


            }
              else {
                Toast.makeText(this@JoinClazz,"未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT).show()
            }


        }
    }


    inner class ConnectionThread(msg: String) : Thread() {
        internal var message: String? = null
        internal var dis: DataInputStream? = null
        internal var dos: DataOutputStream? = null
        init {
            message = msg
        }
        override fun run() {
            while (Pools.socket == null) {
                try {
                    println("开始链接")
                    Pools.socket = Socket("119.23.225.4", 8000)
                    //获取socket的输入输出流
                    dis = DataInputStream(Pools.socket!!.getInputStream())
                    dos = DataOutputStream(Pools.socket!!.getOutputStream())
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            try {
                println("尝试发送")

                PrintWriter(
                    OutputStreamWriter(Pools.socket?.getOutputStream(), "UTF-8"),
                    true
                ).println(message)
                val br = BufferedReader(InputStreamReader(Pools.socket?.getInputStream(), "UTF-8"))
                while (true) {
                    val readline = br.readLine()
                    if (threat?.isInterrupted!!) break
                    if (readline != null) {
                        val b = Bundle()
                        val msg = Message()
                        b.putString("data", readline)
                        msg.data = b
                        handler.sendMessage(msg)
                        break
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}