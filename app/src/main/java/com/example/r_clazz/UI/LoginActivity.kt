package com.example.r_clazz.UI

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import android.widget.*
import com.example.r_clazz.DB.LocalDB
import com.example.r_clazz.DB.Nowusers
import com.example.r_clazz.DB.Users
import com.example.r_clazz.NetWork.Net
import com.example.r_clazz.NetWork.Pools
import com.example.r_clazz.R
import com.example.r_clazz.Service.NetIsActivable
import org.json.JSONObject
import java.io.*
import java.net.Socket

class LoginActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    ViewTreeObserver.OnGlobalLayoutListener, TextWatcher {
    /*
    * 控件
    * */

    lateinit var markme: CheckBox

    private var mIbNavigationBack: ImageButton? = null
    private var mEtLoginUsername: EditText? = null
    private var mEtLoginPwd: EditText? = null
    private var mLlLoginUsername: LinearLayout? = null
    private var mIvLoginUsernameDel: ImageView? = null
    private var mBtLoginSubmit: Button? = null
    private var mLlLoginPwd: LinearLayout? = null
    private var mIvLoginPwdDel: ImageView? = null
    private var mIvLoginLogo: ImageView? = null
    private var mLayBackBar: LinearLayout? = null
    private var mTvLoginForgetPwd: TextView? = null
    private var mBtLoginRegister: Button? = null
    var dos: DataOutputStream? = null
    var dis: DataInputStream? = null
    var messageRecv: String? = null
    var threat: ConnectionThread? = null

    private var mLogoHeight: Int = 0
    private var mLogoWidth: Int = 0

    internal var RU: String? = null
    internal var RP: String? = null

    internal var TAG = "Login"
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    //登陆对话框
    lateinit var progressDialog: ProgressDialog
    /*
    * DataBase
    * */
    internal var dbhelper: LocalDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title()
        setContentView(R.layout.activity_login)

        val intents = Intent(this@LoginActivity, NetIsActivable::class.java)
        startService(intents)
        val intent = intent
        RU = intent.getStringExtra("un")
        RP = intent.getStringExtra("pa")
        Log.d(TAG, "onCreate:   RU$RU")
        Log.d(TAG, "onCreate:   RP$RP")

        //注册dialog

        progressDialog = ProgressDialog(this@LoginActivity)
        markme = findViewById(R.id.markme)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        initView()

        val username = mEtLoginUsername?.text.toString().trim { it <= ' ' }
        val pwd = mEtLoginPwd?.text.toString().trim { it <= ' ' }
        //登录按钮是否可用
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            mBtLoginSubmit?.isClickable = true
            mBtLoginSubmit?.setBackgroundResource(R.drawable.bg_login_submit)
            mBtLoginSubmit?.setTextColor(resources.getColor(R.color.black))
        } else {
            mBtLoginSubmit?.setBackgroundResource(R.drawable.bg_login_submit_lock)
            mBtLoginSubmit?.isClickable = false
            mBtLoginSubmit?.setTextColor(resources.getColor(R.color.account_lock_font_color))
        }
        //数据库初始化
        dbhelper = LocalDB(this@LoginActivity, "User.db", null, 2)
        dbhelper?.getWritableDatabase()


    }

    //初始化视图
    private fun initView() {


        //导航栏+返回按钮
        mLayBackBar = findViewById(R.id.ly_retrieve_bar)
        mIbNavigationBack = findViewById(R.id.ib_navigation_back)

        //logo
        mIvLoginLogo = findViewById(R.id.iv_login_logo)

        //username
        mLlLoginUsername = findViewById(R.id.ll_login_username)
        mEtLoginUsername = findViewById(R.id.et_login_username)
        mIvLoginUsernameDel = findViewById(R.id.iv_login_username_del)

        //passwd
        mLlLoginPwd = findViewById(R.id.ll_login_pwd)
        mEtLoginPwd = findViewById(R.id.et_login_pwd)
        mIvLoginPwdDel = findViewById(R.id.iv_login_pwd_del)

        //提交、注册
        mBtLoginSubmit = findViewById(R.id.bt_login_submit)
        mBtLoginRegister = findViewById(R.id.bt_login_register)

        //忘记密码
        mTvLoginForgetPwd = findViewById(R.id.tv_login_forget_pwd)
        mTvLoginForgetPwd?.setOnClickListener(this)

        val isRemember = preferences?.getBoolean("remember", false)

        //检查是否记住密码
        if (isRemember!! && RU == null && RP == null) {
            val account = preferences?.getString("account", "")
            val pass = preferences?.getString("password", "")
            mEtLoginUsername?.setText(account)
            mEtLoginPwd?.setText(pass)
            markme.isChecked = true
        } else if (RU != null && RP != null) {
            mEtLoginUsername?.setText(RU)
            mEtLoginPwd?.setText(RP)
        }

        mIbNavigationBack?.setVisibility(View.INVISIBLE)
        mIbNavigationBack?.setClickable(false)

        //注册点击事件
        mIbNavigationBack?.setOnClickListener(this)
        mEtLoginUsername?.setOnClickListener(this)
        mIvLoginUsernameDel?.setOnClickListener(this)
        mBtLoginSubmit?.setOnClickListener(this)
        mBtLoginRegister?.setOnClickListener(this)
        mEtLoginPwd?.setOnClickListener(this)
        mIvLoginPwdDel?.setOnClickListener(this)


        //注册其它事件
        mLayBackBar?.getViewTreeObserver()?.addOnGlobalLayoutListener(this)
        mEtLoginUsername?.setOnFocusChangeListener(this)
        mEtLoginUsername?.addTextChangedListener(this)
        mEtLoginPwd?.setOnFocusChangeListener(this)
        mEtLoginPwd?.addTextChangedListener(this)


    }

    //融合状态栏
    private fun title() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    //显示loading对话框
    private fun showloading() {
        progressDialog.setTitle("登陆中")
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(true)
        progressDialog.show()

    }

    //关闭loading
    private fun closeloading() {
        progressDialog.cancel()
    }

    //云端数据库查到资料后放进本地数据库
    private fun addtolocal(
        phone: String,
        password: String,
        name: String,
        identity: String,
        identitycode: String
    ) {
        val db = dbhelper?.writableDatabase
        val values = ContentValues()
        values.put("phone", phone)
        values.put("password", password)
        values.put("identitycode", identitycode)
        values.put("name", name)
        values.put("identity", identity)
        db?.insert("Users", null, values)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ib_navigation_back -> {
            }
            R.id.et_login_username -> {
                mEtLoginPwd?.clearFocus()
                mEtLoginUsername?.isFocusableInTouchMode = true
                mEtLoginUsername?.requestFocus()
            }
            R.id.et_login_pwd -> {
                mEtLoginUsername?.clearFocus()
                mEtLoginPwd?.isFocusableInTouchMode = true
                mEtLoginPwd?.requestFocus()
            }
            R.id.iv_login_username_del ->
                //清空用户名
                mEtLoginUsername?.setText(null)
            R.id.iv_login_pwd_del ->
                //清空密码
                mEtLoginPwd?.setText(null)
            R.id.bt_login_submit ->
                //登录
                loginRequest()
            R.id.bt_login_register -> {
                //注册
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }
            R.id.tv_login_forget_pwd ->
                //忘记密码
                startActivity(Intent(this@LoginActivity, ForgetPwdActivity::class.java))
            else -> {
            }
        }//返回
        //finish();
    }

    //用户名密码焦点改变
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        val id = v.id

        if (id == R.id.et_login_username) {
            if (hasFocus) {
                mLlLoginUsername?.isActivated = true
                mLlLoginPwd?.isActivated = false
            }
        } else {
            if (hasFocus) {
                mLlLoginPwd?.isActivated = true
                mLlLoginUsername?.isActivated = false
            }
        }
    }


    //显示或隐藏logo
    override fun onGlobalLayout() {
        val ivLogo = this.mIvLoginLogo
        val KeypadRect = Rect()

        mLayBackBar?.getWindowVisibleDisplayFrame(KeypadRect)

        val screenHeight = mLayBackBar?.rootView?.height
        val keypadHeight = screenHeight?.minus(KeypadRect.bottom)

        //隐藏logo
        if (keypadHeight!! > 300 && ivLogo?.getTag() == null) {
            val height = ivLogo?.height
            val width = ivLogo?.width
            this.mLogoHeight = height!!
            this.mLogoWidth = width!!

            ivLogo.setTag(true)

            val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
            valueAnimator.setDuration(400).interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                val layoutParams = ivLogo.getLayoutParams()
                layoutParams.height = (height * animatedValue).toInt()
                layoutParams.width = (width * animatedValue).toInt()
                ivLogo.requestLayout()
                ivLogo.setAlpha(animatedValue)
            }

            if (valueAnimator.isRunning) {
                valueAnimator.cancel()
            }
            valueAnimator.start()
        } else if (keypadHeight!! < 300 && ivLogo?.getTag() != null) {
            val height = mLogoHeight
            val width = mLogoWidth

            ivLogo.setTag(null)

            val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator.setDuration(400).interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                val layoutParams = ivLogo.getLayoutParams()
                layoutParams.height = (height * animatedValue).toInt()
                layoutParams.width = (width * animatedValue).toInt()
                ivLogo.requestLayout()
                ivLogo.setAlpha(animatedValue)
            }

            if (valueAnimator.isRunning) {
                valueAnimator.cancel()
            }
            valueAnimator.start()
        }//显示logo
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    //用户名密码输入事件
    override fun afterTextChanged(s: Editable) {
        val username = mEtLoginUsername?.getText().toString().trim { it <= ' ' }
        val pwd = mEtLoginPwd?.getText().toString().trim { it <= ' ' }

        Log.d(TAG, "afterTextChanged: change")
        //是否显示清除按钮

        if (username.length > 0) {
            mIvLoginUsernameDel?.setVisibility(View.VISIBLE)
        } else {
            mIvLoginUsernameDel?.setVisibility(View.INVISIBLE)
        }
        if (pwd.length > 0) {
            mIvLoginPwdDel?.setVisibility(View.VISIBLE)
        } else {
            mIvLoginPwdDel?.setVisibility(View.INVISIBLE)
        }

        //登录按钮是否可用
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            mBtLoginSubmit?.setClickable(true)
            mBtLoginSubmit?.setBackgroundResource(R.drawable.bg_login_submit)
            mBtLoginSubmit?.setTextColor(resources.getColor(R.color.black))
        } else {
            mBtLoginSubmit?.setBackgroundResource(R.drawable.bg_login_submit_lock)
            mBtLoginSubmit?.setClickable(false)
            mBtLoginSubmit?.setTextColor(resources.getColor(R.color.account_lock_font_color))
        }
    }

    //登录
    private fun loginRequest() {
        showloading()
        Toast.makeText(this@LoginActivity, "showloading", Toast.LENGTH_SHORT).show()
        val account = mEtLoginUsername?.getText().toString()
        val pass = mEtLoginPwd?.getText().toString()

        if (account === "" || pass === "") {
            closeloading()
            Toast.makeText(this@LoginActivity, "请输入学号或者密码", Toast.LENGTH_SHORT).show()
        }
        val logins = LocalDB.query_user(account, dbhelper)
        //如果本地数据库有记录,就直接放行,不去云端数据库搞事情了
        if (logins != null && pass == logins.password) {
            println(logins.identitycode + "  " + logins.password)
            editor = preferences?.edit()
            if (markme.isChecked) {//记住密码是否生效
                editor?.putBoolean("remember", true)
                editor?.putString("account", account)
                editor?.putString("password", pass)
                LocalDB.updata_user(account, pass, dbhelper)
            } else
                editor?.clear()
            editor?.apply()
            Toast.makeText(this@LoginActivity, "登陆成功", Toast.LENGTH_SHORT).show()
            closeloading()
            //TODO:
            Nowusers.setPhone(account)
            Nowusers.setName(logins.name)
            Nowusers.setIdentity(logins.identity)
            Nowusers.setIdentitycode(logins.identitycode)
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("user", account)
            threat?.interrupt()
            if (Net.isNetworkAvailable(this)){
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"您的网络似乎开小差了呢",Toast.LENGTH_SHORT).show()
            }


        } else {//本地数据库为空或者密码匹配不上
            //请求云端服务器拉取信息
            var query_map = HashMap<String, String>()
            query_map.put("operation", "QueryUsers")
            query_map.put("identitycode", account)
            query_map.put("pass", pass)
            val query_json = JSONObject(query_map as Map<*, *>)
            if (Net.isNetworkAvailable(this)){
                threat = ConnectionThread(query_json.toString())
                threat?.start()
            }
            else{
                Toast.makeText(this,"您的网络似乎开小差了呢",Toast.LENGTH_SHORT).show()
            }


        }
    }


    //退出时的时间
    private var mExitTime: Long = 0

    //对返回键进行监听
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    //退出方法
    private fun exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this@LoginActivity, "再按一次退出应用", Toast.LENGTH_SHORT).show()
            mExitTime = System.currentTimeMillis()
        } else {
            //用户退出处理
            finish()
            System.exit(0)
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
            if (opreation == "ResponseQuery") {
                val found = json.get("found")
                if (found == "false") {
                    closeloading()
                    Toast.makeText(this@LoginActivity, "从云端数据库获取信息或者登陆失败", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    addtolocal(
                        json.getString("phone"),
                        json.getString("password"),
                        json.getString("name"),
                        json.getString("identity"),
                        json.getString("identitycode")
                    )
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("user", json.getString("identitycode"))
                    closeloading()
                    threat?.interrupt()
                    startActivity(intent)
                    finish()

                }
            } else {
                Toast.makeText(this@LoginActivity, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT).show()
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

            while (Pools.socket == null){
                try {
                    println("开始链接")
                    Pools.socket = Socket("119.23.225.4", 8000)
                    //获取socket的输入输出流
                    dis = DataInputStream(Pools.socket!!.getInputStream())
                    dos  = DataOutputStream(Pools.socket!!.getOutputStream())
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
