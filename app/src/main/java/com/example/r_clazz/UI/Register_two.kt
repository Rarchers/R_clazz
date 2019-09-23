package com.example.r_clazz.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.example.r_clazz.DB.LocalDB
import com.example.r_clazz.NetWork.Net
import com.example.r_clazz.NetWork.Pools
import com.example.r_clazz.R
import org.json.JSONObject
import java.io.*
import java.net.Socket

class Register_two : AppCompatActivity(),View.OnClickListener, TextWatcher {
   private var phonenumber: String? = null
    /*
   * DataBase
   * */
    private lateinit var dbhelper: LocalDB
    private lateinit var bmp: Bitmap
    private var TAG = "RT"
    internal lateinit var name: EditText
    private lateinit var pwd: EditText
    private lateinit var identity_code: EditText
    private lateinit var man: TextView
    private lateinit var woman:TextView
    private lateinit var register: Button
    var threat: ConnectionThread?=null
    private var identity = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_two)
        val drawable = application.resources.getDrawable(R.drawable.nav_icon)
        bmp = Bitmap.createBitmap(drawableToBitmap(drawable))
        findViewById<ImageButton>(R.id.ib_navigation_back).setOnClickListener(this)
        init()
        //数据库初始化
        dbhelper = LocalDB(this@Register_two, "User.db", null, 2)
        dbhelper.writableDatabase
        val intent = intent
        phonenumber = intent.getStringExtra("phones")
        Log.d(TAG, "onCreate: number$phonenumber")
    }
    private fun init() {
        identity_code = findViewById(R.id.et_register_identity_input)
        name = findViewById(R.id.et_register_username)
        pwd = findViewById(R.id.et_register_pwd_input)
        man = findViewById(R.id.tv_register_man)
        woman = findViewById(R.id.tv_register_female)
        register = findViewById(R.id.bt_register_submit)

        register.setOnClickListener(this)
        man.setOnClickListener(this)
        woman.setOnClickListener(this)

        name.addTextChangedListener(this)
        pwd.addTextChangedListener(this)
        identity_code.addTextChangedListener(this)

        val phone = name.text.toString().trim { it <= ' ' }
        val codes = pwd.text.toString().trim { it <= ' ' }
        val incode = identity_code.text.toString().trim { it <= ' ' }

        Log.d(TAG, "afterTextChanged: 检查是否开放注册")
        //注册按钮是否可用
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(codes) && !TextUtils.isEmpty(incode) && !TextUtils.isEmpty(identity)) {
            register.isClickable = true
            register.setBackgroundResource(R.drawable.bg_login_submit)
            register.setTextColor(resources.getColor(R.color.white))
        } else {
            register.isClickable = false
            register.setBackgroundResource(R.drawable.bg_login_submit_lock)
            register.setTextColor(resources.getColor(R.color.account_lock_font_color))
        }
    }


    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

    }

    override fun afterTextChanged(editable: Editable) {
        val phone = name.text.toString().trim { it <= ' ' }
        val codes = pwd.text.toString().trim { it <= ' ' }
        val incodes = identity_code.text.toString().trim { it <= ' ' }
        Log.d(TAG, "afterTextChanged: 检查是否开放注册")
        //注册按钮是否可用
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(codes) && !TextUtils.isEmpty(incodes) && !TextUtils.isEmpty(identity)) {
            register.isClickable = true
            register.setBackgroundResource(R.drawable.bg_login_submit)
            register.setTextColor(resources.getColor(R.color.white))
        } else {
            register.isClickable = false
            register.setBackgroundResource(R.drawable.bg_login_submit_lock)
            register.setTextColor(resources.getColor(R.color.account_lock_font_color))
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_register_man -> {
                identity = "教师"
                Toast.makeText(applicationContext, "教师", Toast.LENGTH_SHORT).show()
                val student = resources.getDrawable(R.drawable.student)
                val teacherclick = resources.getDrawable(R.drawable.teacher_click)
                teacherclick.setBounds(0,0,teacherclick.minimumWidth,teacherclick.minimumHeight)
                student.setBounds(0,0,student.minimumWidth,student.minimumHeight)
                findViewById<TextView>( R.id.tv_register_man ).setCompoundDrawables(teacherclick,null,null,null)
                findViewById<TextView>( R.id.tv_register_female ).setCompoundDrawables(student,null,null,null)

                val phone = name.text.toString().trim { it <= ' ' }
                val codes = pwd.text.toString().trim { it <= ' ' }
                val incodes = identity_code.text.toString().trim { it <= ' ' }

                Log.d(TAG, "afterTextChanged: 检查是否开放注册")
                //注册按钮是否可用
                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(codes) && !TextUtils.isEmpty(
                        incodes
                    ) && !TextUtils.isEmpty(identity)
                ) {
                    register.isClickable = true
                    register.setBackgroundResource(R.drawable.bg_login_submit)
                    register.setTextColor(resources.getColor(R.color.white))
                } else {
                    register.isClickable = false
                    register.setBackgroundResource(R.drawable.bg_login_submit_lock)
                    register.setTextColor(resources.getColor(R.color.account_lock_font_color))
                }
            }
            R.id.tv_register_female -> {
                identity = "学生"
                Toast.makeText(applicationContext, "学生", Toast.LENGTH_SHORT).show()
                val studentclick = resources.getDrawable(R.drawable.student_click)
                val teacher = resources.getDrawable(R.drawable.teacher)
                teacher.setBounds(0,0,teacher.minimumWidth,teacher.minimumHeight)
                studentclick.setBounds(0,0,studentclick.minimumWidth,studentclick.minimumHeight)
                findViewById<TextView>( R.id.tv_register_man ).setCompoundDrawables(teacher,null,null,null)
                findViewById<TextView>( R.id.tv_register_female ).setCompoundDrawables(studentclick,null,null,null)

                val phones = name.text.toString().trim { it <= ' ' }
                val codess = pwd.text.toString().trim { it <= ' ' }
                val incodess = identity_code.text.toString().trim { it <= ' ' }

                Log.d(TAG, "afterTextChanged: 检查是否开放注册")
                //注册按钮是否可用
                if (!TextUtils.isEmpty(phones) && !TextUtils.isEmpty(codess) && !TextUtils.isEmpty(
                        incodess
                    ) && !TextUtils.isEmpty(identity)
                ) {
                    register.isClickable = true
                    register.setBackgroundResource(R.drawable.bg_login_submit)
                    register.setTextColor(resources.getColor(R.color.white))
                } else {
                    register.isClickable = false
                    register.setBackgroundResource(R.drawable.bg_login_submit_lock)
                    register.setTextColor(resources.getColor(R.color.account_lock_font_color))
                }
            }
            R.id.bt_register_submit -> regist()
        }
    }

    private fun regist() {
        Log.d(TAG, "regist: 开始注册")
        val uname = name.text.toString().trim { it <= ' ' }
        val pass = pwd.text.toString().trim { it <= ' ' }
        val incodes = identity_code.text.toString().trim { it <= ' ' }

        Log.d(TAG, "regist: name  $uname")
        Log.d(TAG, "regist: pass  $pass")
        Log.d(TAG, "regist: 名字判断" + !TextUtils.isEmpty(uname))
        Log.d(TAG, "regist: 密码判断" + !TextUtils.isEmpty(pass))
        if (TextUtils.isEmpty(uname) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(incodes)) {
            Toast.makeText(applicationContext, "名字不能为空", Toast.LENGTH_SHORT).show()
            return
        } else if (!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(incodes) && TextUtils.isEmpty(
                pass
            )
        ) {
            Toast.makeText(applicationContext, "密码不能为空", Toast.LENGTH_SHORT).show()
            return
        } else if (!TextUtils.isEmpty(uname) && TextUtils.isEmpty(incodes) && !TextUtils.isEmpty(
                pass
            )
        ) {
            Toast.makeText(applicationContext, "请输入身份证号", Toast.LENGTH_SHORT).show()
            return
        } else if (!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(pass)) {
            if (pass.length < 6) {
                Toast.makeText(applicationContext, "请输入至少六位新密码", Toast.LENGTH_SHORT).show()
                return
            } else {
                Log.d(TAG, "regist: 添加")
                LocalDB.insert_user(
                    applicationContext,
                    phonenumber,
                    pass,
                    identity,
                    incodes,
                    uname,
                    dbhelper
                )
                val insert_map = HashMap<String, String>()
                insert_map.put("operation", "InsertUsers")
                insert_map.put("name",uname)
                insert_map.put("password", pass)
                insert_map.put("phone", phonenumber.toString())
                insert_map.put("identity",identity)
                insert_map.put("identitycode", incodes)
                val insetr_json = JSONObject(insert_map as Map<*, *>)
                Log.d("sss",insetr_json.toString())

                threat = ConnectionThread(insetr_json.toString())
                threat?.start()

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
            println("收到信息")
            val opreation = json.get("operation")
            if (opreation == "ResponseInsert"){
                val found = json.get("response")
                if (found=="false") {
                    Toast.makeText(this@Register_two, "注册失败，请检查网络连接或重复注册", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this@Register_two, "注册成功", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Register_two, LoginActivity::class.java)
                    intent.putExtra("un",json.getString("identitycode"))
                    intent.putExtra("pa", json.getString("password"))
                    startActivity(intent)
                    finish()
                }
            }
            else{Toast.makeText(this@Register_two,"未知错误，请联系开发者获得更多支持",Toast.LENGTH_SHORT).show()}


        }
    }


    inner  class ConnectionThread(msg: String) : Thread() {
        internal var message: String? = null
        internal var dis: DataInputStream? = null
        internal var dos: DataOutputStream? = null

        init {
            message = msg
        }

        override  fun run() {
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
                PrintWriter(OutputStreamWriter(Pools.socket?.getOutputStream(), "UTF-8"), true).println(message)
                val br = BufferedReader(InputStreamReader(Pools.socket?.getInputStream(),"UTF-8"))
                while (true){
                    val readline = br.readLine()
                    if (threat?.isInterrupted!!)break
                    if (readline!=null){
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



    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        // 取 drawable 的颜色格式
        val config = if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        val bitmap = Bitmap.createBitmap(w, h, config)
        //建立对应 bitmap 的画布
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        // 把 drawable 内容画到画布中
        drawable.draw(canvas)
        return bitmap
    }


}
