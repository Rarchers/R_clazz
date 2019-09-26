package com.example.r_clazz.Fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.r_clazz.Adapters.Course_Adapter
import com.example.r_clazz.Been.Course_Been
import com.example.r_clazz.DB.Nowusers
import com.example.r_clazz.NetWork.Pools

import com.example.r_clazz.R
import com.example.r_clazz.UI.CrouseForTeacher
import com.example.r_clazz.UI.MainActivity
import kotlinx.android.synthetic.main.fragment_classes__teacher.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.Socket
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class Classes_Teacher : Fragment(), View.OnClickListener {

    var threat: ConnectionThread? = null
    var thread: ConnectionThread? = null
    private var activity: Activity? = null
    private var datalist = ArrayList<Course_Been>()
    private var adapter: Course_Adapter? = null
    private var list: List<String>? = null
    private var listsize = 0
    private var listcount = 0
    private var refreashs: SwipeRefreshLayout? = null
    lateinit var progressDialog: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_classes__teacher, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this.getActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(activity)
        refreashs = view.findViewById(R.id.refreash)
        addcourse.setOnClickListener(this)

        mycourses.setOnItemClickListener { parent, view, position, id ->
            val course = datalist.get(position)
            val intent = Intent(activity, CrouseForTeacher::class.java)
            intent.putExtra("course_code", course.course_code)
            startActivity(intent)
        }



        mycourses.setOnItemLongClickListener { parent, view, position, id ->
            println("长按点击")
            val course = datalist.get(position)
            val delet = AlertDialog.Builder(activity)
            delet.setIcon(R.drawable.ic_delete)
            delet.setTitle("此操作将删除本课程")
            delet.setMessage("您确定要删除吗")
            delet.setPositiveButton("是的", DialogInterface.OnClickListener { dialog, which ->
                for (i in 0..datalist.size - 1) datalist.remove(datalist[0])
                val deleteCourse = HashMap<String, String>()
                deleteCourse["operation"] = "DeleteCourse_teacehr"
                deleteCourse["course_code"] = course.course_code
                val deleteJson = JSONObject(deleteCourse)
                showloading()
                threat = ConnectionThread(deleteJson.toString())
                println("删除课程")
                threat?.start()

            })
            delet.setNegativeButton("取消") { dialog, which -> }
            delet.show()

            true
        }
        refreashs?.setColorSchemeResources(R.color.colorPrimary)
        refreashs?.setOnRefreshListener {
            for (i in 0..datalist.size - 1) datalist.remove(datalist[0])
            println("开始刷新")
            init()
        }
        showrefreashloading()
        init()
    }

    //关闭loading
    private fun closeloading() {
        progressDialog.cancel()
    }

    //显示loading对话框
    private fun showloading() {
        progressDialog.setTitle("删除中")
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(true)
        progressDialog.show()
    }
    //显示loading对话框
    private fun showrefreashloading() {
        progressDialog.setTitle("获取数据中")
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(true)
        progressDialog.show()
    }

    fun init() {
        val queryCourse_Teacher = HashMap<String, String>()
        queryCourse_Teacher["operation"] = "QueryCourse"
        queryCourse_Teacher["clazz_code"] = Nowusers.getIdentitycode()
        val queryJson = JSONObject(queryCourse_Teacher)
        threat = ConnectionThread(queryJson.toString())
        println("开始查询课程")
        threat?.start()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addcourse -> {
                for (i in 0..datalist.size - 1) datalist.remove(datalist[0])
                registerClazz()
            }
        }
    }

    //TODO:服务器上的处理逻辑~~~~~~~~~~~~~
    fun registerClazz() {
        val clazz_code = getRandomString()
        val register_class = HashMap<String, String>()
        register_class["operation"] = "CreateClazz"
        register_class["course_code"] = clazz_code
        register_class["course_name"] = "新建课程"
        register_class["course_teacehr"] = Nowusers.getIdentitycode()
        register_class["course_studentinfo"] = ""
        val register_json = JSONObject(register_class)
        threat = ConnectionThread(register_json.toString())
        println("开始添加课程")
        threat?.start()
    }


    fun getRandomString(): String {
        val str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random()
        var sb = StringBuffer()
        for (i in 0..5) {
            sb.append(str[random.nextInt(62)])
        }
        return sb.toString()
    }


    fun refreash( codes:String) {
        val querycourse_info = HashMap<String, String>()
        querycourse_info["'operation'"] = "'QueryCourseInfo'"
        querycourse_info["'clazz_code'"] = "'$codes'"
        thread = ConnectionThread(querycourse_info.toString())
        thread?.start()


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
            if (opreation == "ResponseCourse") {
                val found = json.get("response")
                if (found == "false") {
                    adapter = Course_Adapter(context!!, R.layout.clazz_item, datalist)
                    mycourses.adapter = adapter
                    println("没有课程")
                    closeloading()
                    refreash.setRefreshing(false)
                } else {
                    println("有课程")
                    var clazz = json.get("clazz")
                    var res = clazz.toString()
                    res = res.substring(0, res.length - 1)
                    list = res.split(",")
                    refreash(res)
                }
            } else if (opreation == "ResponseCourseInfo") {
                println("查询课程详细信息")
                var classArray: JSONArray = json.getJSONArray("Clazzes")
                for (i in 0 until classArray.length()) {
                    val clazz: JSONObject = classArray[i] as JSONObject
                    val course = Course_Been(
                        clazz.getString("course_code"),
                        clazz.getString("course_name"),
                        clazz.getString("course_teacehr"),
                        clazz.getString("course_studentinfo")
                    )
                    datalist.add(course)

                }
                println("listcount = $listcount,       listsize = $listsize")
                closeloading()
                Toast.makeText(activity, "加载成功", Toast.LENGTH_SHORT).show()
                refreash.setRefreshing(false)
                adapter = Course_Adapter(context!!, R.layout.clazz_item, datalist)
                mycourses.adapter = adapter

            } else if (opreation == "ResponseCreate") {
                val success = json.getString("success")
                if (success == "true") {
                    init()
                } else registerClazz()
            } else if (opreation == "ResponseDelete") {
                val success = json.getString("success")
                if (success == "true") {
                    closeloading()
                    init()
                    Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(activity, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT).show()
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
            if (Pools.socket == null) {
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
