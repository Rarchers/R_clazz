package com.example.r_class.view.main.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.r_class.R
import com.example.r_class.model.adapters.CourseAdapter
import com.example.r_class.net.Connection
import com.example.r_class.utils.SPUtils
import com.example.r_class.view.course.CourseForTeacher
import com.example.r_class.view.joinclass.JoinClazz
import com.example.r_class.viewmodel.CourseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Course.newInstance] factory method to
 * create an instance of this fragment.
 */
class Course : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var freshData : SwipeRefreshLayout
    lateinit var lvCourse : ListView
    lateinit var adapter : CourseAdapter
    lateinit var spUtils: SPUtils
    lateinit var viewModel: CourseViewModel
    lateinit var progressDialog: ProgressDialog
    lateinit var addCourse :ImageView
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course, container, false)
        initView(view)
        initVariables()
        freshData.setColorSchemeResources(R.color.colorPrimary)

        showloading()
        try {
            queryCourse()
        }catch (e:Exception){
            closeloading()
        }

        return view
    }


    private fun initView(view: View){
        freshData = view.findViewById(R.id.freshLayout)
        lvCourse = view.findViewById(R.id.lv_course)
        addCourse = view.findViewById(R.id.addCourse)

        freshData.setOnRefreshListener {
            viewModel.freshCourse()
            queryCourse()
        }

        addCourse.setOnClickListener {

            if (spUtils.identify == "教师") registerClazz()
            else startActivity(Intent(activity,JoinClazz::class.java))

        }

        lvCourse.setOnItemLongClickListener { parent, view, position, id ->

            println("长按点击")
            val course = viewModel.course.value?.get(position)
            val delet = AlertDialog.Builder(activity)
            delet.setIcon(R.drawable.ic_delete)
            delet.setTitle("此操作将删除本课程")
            delet.setMessage("您确定要删除吗")
            delet.setPositiveButton("是的", DialogInterface.OnClickListener { dialog, which ->
                viewModel.freshCourse()
                val deleteCourse = HashMap<String, String>()
                deleteCourse["operation"] = "DeleteCourse_teacehr"
                if (course != null) {
                    deleteCourse["course_code"] = course.course_code
                }else{
                    return@OnClickListener
                }
                val deleteJson = JSONObject(deleteCourse as Map<*, *>)
                showloading()
                deleteClazz(deleteJson.toString())
                closeloading()
            })
            delet.setNegativeButton("取消") { dialog, which -> }
            delet.show()
            true
        }


        lvCourse.setOnItemClickListener { parent, view, position, id ->
            val course = viewModel.course.value?.get(position)

            if (spUtils.identify == "教师"){
                val intent = Intent(activity, CourseForTeacher::class.java)
                intent.putExtra("course_code", course?.course_code)
                startActivity(intent)
            }

        }

    }


    fun deleteClazz(jsonString:String){
        CoroutineScope(Dispatchers.Main).launch {
            val res = Connection().get(jsonString)
            val json = JSONObject(res)
            println("收到信息+ $json")
            val operation = json.get("operation")
            if (operation == "ResponseDelete"){
                val success = json.getString("success")
                if (success == "true") {
                    closeloading()
                    queryCourse()
                    Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(activity, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(activity, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun registerClazz() {
        val clazz_code = getRandomString()
        val register_class = HashMap<String, String>()
        register_class["operation"] = "CreateClazz"
        register_class["course_code"] = clazz_code
        register_class["course_name"] = "新建课程"
        register_class["course_teacehr"] = spUtils.userName
        register_class["course_studentinfo"] = ""
        val register_json = JSONObject(register_class as Map<*, *>)
        CoroutineScope(Dispatchers.Main).launch {
            val res = Connection().get(register_json.toString())
            val json = JSONObject(res)
            println("收到信息+ $json")
            val operation = json.get("operation")
            if (operation == "ResponseCreate") {
                val success = json.getString("success")
                if (success == "true") {
                    queryCourse()
                } else {
                    Toast.makeText(context,"新建课程失败",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(activity, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT).show()
            }

        }

    }









    private fun initVariables(){
        activity?.let {
            spUtils = SPUtils(it)
            viewModel = ViewModelProvider(it).get(CourseViewModel::class.java)
            viewModel.freshCourse()
            progressDialog = ProgressDialog(it)
            adapter = viewModel.course.value?.let { it1 -> CourseAdapter(it, it1) }!!
            lvCourse.adapter = adapter

        }
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

    private fun queryCourse(){
        val queryCourse = HashMap<String, String>()
        queryCourse["operation"] = "QueryCourse"
        queryCourse["clazz_code"] = spUtils.userName
        println("查询！！！！！！！！！！！！！！！！！！！！")
        val queryJson = JSONObject(queryCourse as Map<*, *>)
        CoroutineScope(Dispatchers.Main).launch {
            val res = Connection().get(queryJson.toString())
            println(res)
            val json = JSONObject(res)
            val operation = json.get("operation")
            if (operation == "ResponseCourse") {
                val found = json.get("response")
                if (found == "false") {
                    viewModel.freshCourse()
                    adapter.notifyDataSetChanged()
                    println("没有课程")
                    closeloading()
                    freshData.isRefreshing = false
                } else {
                    viewModel.freshCourse()
                    adapter.notifyDataSetChanged()
                    println("有课程")
                    val clazz = json.get("clazz")
                    var res = clazz.toString()
                    res = res.substring(0, res.length - 1)
                    queryCourseInfo(res)
                }
            } else {
                Toast.makeText(activity, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT).show()
            }


        }
    }

    fun queryCourseInfo(codes:String){
        val querycourse_info = HashMap<String, String>()
        querycourse_info["'operation'"] = "'QueryCourseInfo'"
        querycourse_info["'clazz_code'"] = "'$codes'"
        CoroutineScope(Dispatchers.Main).launch {
            val res = Connection().get(querycourse_info.toString())
            println(querycourse_info.toString())
            println(res)
            val json = JSONObject(res)
            val operation = json.get("operation")
            if (operation == "ResponseCourseInfo") {
                println("查询课程详细信息")
                var classArray: JSONArray = json.getJSONArray("Clazzes")
                for (i in 0 until classArray.length()) {
                    val clazz: JSONObject = classArray[i] as JSONObject
                    val course = com.example.r_class.model.entity.Course(
                        course_code = clazz.getString("course_code"),
                        course_name = clazz.getString("course_name"),
                        course_teacher = clazz.getString("course_teacehr"),
                        course_studentInfo = clazz.getString("course_studentinfo")
                    )
                    viewModel.addCourse(course)

                }
                closeloading()
                Toast.makeText(activity, "加载成功", Toast.LENGTH_SHORT).show()
                freshData.isRefreshing = false
                adapter.notifyDataSetChanged()

            }else{
                Toast.makeText(activity, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT).show()
            }
        }
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











    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Course.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Course().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}