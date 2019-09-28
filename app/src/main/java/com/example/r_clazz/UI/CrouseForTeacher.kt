package com.example.r_clazz.UI

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.r_clazz.Been.Course_Been
import com.example.r_clazz.DB.Nowusers
import com.example.r_clazz.NetWork.Net
import com.example.r_clazz.NetWork.Pools
import com.example.r_clazz.R
import kotlinx.android.synthetic.main.activity_crouse_for_teacher.*
import kotlinx.android.synthetic.main.course_item.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.Socket
import android.widget.ArrayAdapter




class CrouseForTeacher : AppCompatActivity() {
    var threat: ConnectionThread? = null
    var course: Course_Been? = null
    var editable = false

    var lock: CardView? = null
    var shoutup: CardView? = null
    var release: CardView? = null

    var lockname: TextView? = null
    var shoutupname: TextView? = null
    var releasename: TextView? = null

    var lockpic: ImageView? = null
    var shoutuppic: ImageView? = null
    var releasepic: ImageView? = null
    var listview: ListView? = null
    var datalist = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crouse_for_teacher)
        lock = findViewById(R.id.tlock)
        shoutup = findViewById(R.id.tshoutup)
        release = findViewById(R.id.trelease)
        lockpic = lock?.findViewById(R.id.civ)
        shoutuppic = shoutup?.findViewById(R.id.civ)
        releasepic = release?.findViewById(R.id.civ)
        lockname = lock?.findViewById(R.id.ctv)
        shoutupname = shoutup?.findViewById(R.id.ctv)
        releasename = release?.findViewById(R.id.ctv)
        listview = findViewById(R.id.joinstudent)



        lockpic?.setImageResource(R.drawable.lock_glyph_16)
        releasepic?.setImageResource(R.drawable.unlocked_glyph_16)
        shoutuppic?.setImageResource(R.drawable.preferences_glyph_16)
        lockname?.text = "锁屏"
        shoutupname?.text = "静音"
        releasename?.text = "解锁"

        lock?.setOnClickListener {
            val codes = intent.getStringExtra("course_code")
            val querycourse_info = HashMap<String, String>()
            querycourse_info["'operation'"] = "'LockPhone'"
            querycourse_info["'clazz_code'"] = "'$codes'"
            if (Net.isNetworkAvailable(this)) {
                threat = ConnectionThread(querycourse_info.toString())
                threat?.start()
            } else {
                Pools.socket = null
                Toast.makeText(this, "您的网络似乎开小差了呢", Toast.LENGTH_SHORT).show()
            }
        }

        shoutup?.setOnClickListener {
            val codes = intent.getStringExtra("course_code")
            val querycourse_info = HashMap<String, String>()
            querycourse_info["'operation'"] = "'ShoutUp'"
            querycourse_info["'clazz_code'"] = "'$codes'"
            if (Net.isNetworkAvailable(this)) {
                threat = ConnectionThread(querycourse_info.toString())
                threat?.start()
            } else {
                Pools.socket = null
                Toast.makeText(this, "您的网络似乎开小差了呢", Toast.LENGTH_SHORT).show()
            }
        }

        release?.setOnClickListener {
            val codes = intent.getStringExtra("course_code")
            val querycourse_info = HashMap<String, String>()
            querycourse_info["'operation'"] = "'ReleasePhone'"
            querycourse_info["'clazz_code'"] = "'$codes'"
            if (Net.isNetworkAvailable(this)) {
                threat = ConnectionThread(querycourse_info.toString())
                threat?.start()
            } else {
                Pools.socket = null
                Toast.makeText(this, "您的网络似乎开小差了呢", Toast.LENGTH_SHORT).show()
            }
        }


        val codes = intent.getStringExtra("course_code")
        val querycourse_info = HashMap<String, String>()
        querycourse_info["'operation'"] = "'QueryCourseInfo'"
        querycourse_info["'clazz_code'"] = "'$codes'"
        if (Net.isNetworkAvailable(this)) {
            threat = ConnectionThread(querycourse_info.toString())
            threat?.start()
        } else {
            Pools.socket = null
            Toast.makeText(this, "您的网络似乎开小差了呢", Toast.LENGTH_SHORT).show()
        }
        change.setOnClickListener {
            editable = !editable
            if (editable) {
                course_name.isFocusableInTouchMode = true
                course_name.isFocusable = true
                course_name.requestFocus()
            } else {

                val course = course_name.text
                course_name.isFocusable = false
                course_name.isFocusableInTouchMode = false
                val updateName = HashMap<String, String>()
                updateName["'operation'"] = "'UpdateClazz'"
                updateName["'clazz_code'"] = "'$codes'"
                updateName["clazz_name"] = "'$course'"
                if (Net.isNetworkAvailable(this)) {
                    threat = ConnectionThread(updateName.toString())
                    threat?.start()
                } else {
                    Pools.socket = null
                    Toast.makeText(this, "您的网络似乎开小差了呢", Toast.LENGTH_SHORT).show()
                }
            }

        }


    }


    fun init(course: Course_Been) {
        course_name.setText(course.course_name)
        if (!editable) {
            course_name.isFocusable = false
            course_name.isFocusableInTouchMode = false
        } else {
            course_name.isFocusableInTouchMode = true
            course_name.isFocusable = true
            course_name.requestFocus()
        }
        course_teacher.text = Nowusers.getName()
        course_codes.text = intent.getStringExtra("course_code")
        var res = course.course_studentinfo
        var list: List<String>?
        if (res != "") {
            res = res.substring(0, res.length - 1)
            list = res.split(",")
            clazz_number.text = list.size.toString()
        } else clazz_number.text = "0"


    }

    //TODO:刷新群聊学生
    fun refreashlist(){
        listview?.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, datalist)
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
            } else if (opreation == "ResponseUpdate") {
                val success = json.getString("success")
                if (success == "true") {
                    Toast.makeText(this@CrouseForTeacher, "修改成功", Toast.LENGTH_SHORT)
                }
            }else if (opreation == "JoinStudent"){
                //TODO:添加学生
            }else if(opreation =="DeleteStudent"){
                //TODO:学生退出群聊
            }
            else {
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
            Pools.socket = null
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
