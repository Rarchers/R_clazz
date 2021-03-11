package com.example.r_class.view.course

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.r_class.R
import com.example.r_class.model.entity.Course
import com.example.r_class.model.entity.User
import com.example.r_class.net.Connection
import com.example.r_class.utils.SPUtils
import com.example.r_class.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class CourseForTeacher : AppCompatActivity() {
    var course: Course? = null
    var editable = false

    lateinit var lock: CardView
    lateinit var shoutup: CardView
    lateinit var release: CardView

    lateinit var lockname: TextView
    lateinit var  shoutupname: TextView
    lateinit var  releasename: TextView

    lateinit var  lockpic: ImageView
    lateinit var  shoutuppic: ImageView
    lateinit var  releasepic: ImageView
    lateinit var  listview: ListView
    var  datalist = ArrayList<String>()
    lateinit var connection: Connection

    lateinit var course_codes:TextView
    lateinit var course_name:EditText

    lateinit var course_teacher:TextView
    lateinit var clazz_number:TextView
    lateinit var change:Button

    lateinit var spUtils: SPUtils
    lateinit var adapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_for_teacher)

        initView()
        initVariables()
        initClick()

        initData()

    }

    fun initVariables(){
        connection = Connection()
        spUtils = SPUtils(this)
    }


    fun initView(){
        lock = findViewById(R.id.tlock)
        shoutup = findViewById(R.id.tshoutup)
        release = findViewById(R.id.trelease)
        lockpic = lock.findViewById(R.id.civ)
        shoutuppic = shoutup.findViewById(R.id.civ)
        releasepic = release.findViewById(R.id.civ)
        lockname = lock.findViewById(R.id.ctv)
        shoutupname = shoutup.findViewById(R.id.ctv)
        releasename = release.findViewById(R.id.ctv)
        listview = findViewById(R.id.joinstudent)
        course_codes = findViewById(R.id.course_codes)
        course_name = findViewById(R.id.course_name)
        course_teacher = findViewById(R.id.course_teacher)
        clazz_number = findViewById(R.id.clazz_number)
        change = findViewById(R.id.change)
        lockpic.setImageResource(R.drawable.ic_baseline_lock_24)
        releasepic.setImageResource(R.drawable.ic_baseline_lock_open_24)
        shoutuppic.setImageResource(R.drawable.ic_baseline_voice_over_off_24)
        lockname.text = "锁屏"
        shoutupname.text = "静音"
        releasename.text = "解锁"

    }


    @SuppressLint("ShowToast")
    fun initClick(){
        lock.setOnClickListener {
            val codes = intent.getStringExtra("course_code")
            val querycourse_info = HashMap<String, String>()
            querycourse_info["'operation'"] = "'LockPhone'"
            querycourse_info["'clazz_code'"] = "'$codes'"
            CoroutineScope(Dispatchers.Main).launch {
                connection.get(querycourse_info.toString())
            }
        }

        release.setOnClickListener {
            val codes = intent.getStringExtra("course_code")
            val querycourse_info = HashMap<String, String>()
            querycourse_info["'operation'"] = "'ReleasePhone'"
            querycourse_info["'clazz_code'"] = "'$codes'"
            CoroutineScope(Dispatchers.Main).launch {
                connection.get(querycourse_info.toString())
            }
        }

        shoutup.setOnClickListener {
            val codes = intent.getStringExtra("course_code")
            val querycourse_info = HashMap<String, String>()
            querycourse_info["'operation'"] = "'ShoutUp'"
            querycourse_info["'clazz_code'"] = "'$codes'"
            CoroutineScope(Dispatchers.IO).launch {
                connection.get(querycourse_info.toString())
            }
        }

        change.setOnClickListener {
            editable = !editable
            if (editable) {
                course_name.isFocusableInTouchMode = true
                course_name.isFocusable = true
                course_name.requestFocus()
                change.text = "确认"
            } else {
                val course = course_name.text
                course_name.isFocusable = false
                course_name.isFocusableInTouchMode = false
                val updateName = HashMap<String, String>()
                updateName["'operation'"] = "'UpdateClazz'"
                updateName["'clazz_code'"] = "'${intent.getStringExtra("course_code")}'"
                updateName["clazz_name"] = "'$course'"
                CoroutineScope(Dispatchers.Main).launch {
                    val result = connection.get(updateName.toString())
                    val json = JSONObject(result)
                    val opreation = json.get("operation")
                    if (opreation == "ResponseUpdate"){
                        val success = json.getString("success")
                        if (success == "true") {
                            Toast.makeText(this@CourseForTeacher, "修改成功", Toast.LENGTH_SHORT)
                        }
                    }
                }
                change.text = "修改信息"
            }
        }
    }


    suspend fun init(course: Course) {
        course_name.setText(course.course_name)
        if (!editable) {
            course_name.isFocusable = false
            course_name.isFocusableInTouchMode = false
        } else {
            course_name.isFocusableInTouchMode = true
            course_name.isFocusable = true
            course_name.requestFocus()
        }
        course_teacher.text = spUtils.name
        course_codes.text = intent.getStringExtra("course_code")
        var res = course.course_studentInfo
        var list: List<String>?
        if (res != "") {
            res = res.substring(0, res.length - 1)
            list = res.split(",")
            clazz_number.text = list.size.toString()

            for (i in list){
                var query_map = HashMap<String, String>()
                query_map["operation"] = "\"QueryUsers\""
                query_map["identitycode"] = i
                query_map["pass"] = "\"123\""
                val result = connection.get(query_map.toString())
                val json = JSONObject(result)
                val operation = json["operation"]
                if (operation == "ResponseQuery") {
                    val name = json.getString("name")
                    datalist.add("学号：$i 姓名：$name")
                }
            }
            setListViewHeightBasedOnChildren(listview)
            adapter.notifyDataSetChanged()
        } else clazz_number.text = "0"



    }

    fun initData(){

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, datalist)
        listview.adapter = adapter


        val codes = intent.getStringExtra("course_code")
        val querycourse_info = HashMap<String, String>()
        querycourse_info["'operation'"] = "'QueryCourseInfo'"
        querycourse_info["'clazz_code'"] = "'$codes'"
        CoroutineScope(Dispatchers.Main).launch {
            val result = connection.get(querycourse_info.toString())
            val json = JSONObject(result)
            val operation = json.get("operation")
            if (operation == "ResponseCourseInfo"){
                println("查询课程详细信息")
                var classArray: JSONArray = json.getJSONArray("Clazzes")
                for (i in 0 until classArray.length()) {
                    val clazz: JSONObject = classArray[i] as JSONObject
                    course = Course(
                        clazz.getString("course_code"),
                        clazz.getString("course_name"),
                        clazz.getString("course_teacehr"),
                        clazz.getString("course_studentinfo")
                    )
                }
                init(course!!)
            }
        }

    }


    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem: View = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0) // 获取item高度
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        // 最后再加上分割线的高度和padding高度，否则显示不完整。
        params.height =
            totalHeight + listView.dividerHeight * (listAdapter.count - 1) + listView.paddingTop + listView.paddingBottom
        listView.layoutParams = params
    }
}