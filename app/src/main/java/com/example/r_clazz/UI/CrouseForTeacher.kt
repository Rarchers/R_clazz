package com.example.r_clazz.UI

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.example.r_clazz.Been.Course_Been
import com.example.r_clazz.DB.Nowusers
import com.example.r_clazz.NetWork.Net
import com.example.r_clazz.NetWork.Pools
import com.example.r_clazz.R
import kotlinx.android.synthetic.main.activity_crouse_for_teacher.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.Socket

class CrouseForTeacher : AppCompatActivity() {
    var threat: ConnectionThread? = null
    var course : Course_Been? = null
    var editable = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crouse_for_teacher)
        val codes = intent.getStringExtra("course_code")
        val querycourse_info = HashMap<String, String>()
        querycourse_info["'operation'"] = "'QueryCourseInfo'"
        querycourse_info["'clazz_code'"] = "'$codes'"
        if (Net.isNetworkAvailable(this)){
            threat = ConnectionThread(querycourse_info.toString())
            threat?.start()
        }else{
            Toast.makeText(this,"您的网络似乎开小差了呢",Toast.LENGTH_SHORT).show()

        }





















    }

    fun init(course:Course_Been){
        course_name.setText(course.course_name)
        if (!editable){
            course_name.isFocusable = false
            course_name.isFocusableInTouchMode = false
        }else{
            course_name.isFocusableInTouchMode = true
            course_name.isFocusable = true
            course_name.requestFocus()
        }
        course_teacher.text = Nowusers.getName()
        course_codes.text = intent.getStringExtra("course_code")

    }


    var handler = @SuppressLint("HandlerLeak")
    object : Handler() {     //此处的object 要加，否则无法重写 handlerMessage
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val b = msg?.data  //获取消息中的Bundle对象
            val str = b?.getString("data")//获取键为data的字符串的值
            val json = JSONObject(str)
            println("收到信息")
            val opreation = json.get("operation")
            if (opreation == "ResponseQuery") {

            } else if (opreation == "ResponseCourseInfo") {
                println("查询课程详细信息")
                var classArray: JSONArray = json.getJSONArray("Clazzes")
                for (i in 0 until classArray.length()) {
                    val clazz: JSONObject = classArray[i] as JSONObject
                        course = Course_Been(
                        clazz.getString("course_code"),
                        clazz.getString("course_name"),
                        clazz.getString("course_teacehr"),
                        clazz.getString("course_studentinfo")
                    )
                }
                init(course!!)
            } else {
                Toast.makeText(this@CrouseForTeacher, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT)
                    .show()
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
            Pools.socket =null
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
                PrintWriter(OutputStreamWriter(Pools.socket?.getOutputStream(), "UTF-8"),
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
