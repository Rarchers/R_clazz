package com.example.r_class.view.login

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.r_class.R
import com.example.r_class.dialogs.LoadingDialog
import com.example.r_class.model.daos.UserDao
import com.example.r_class.model.db.UserDB
import com.example.r_class.model.entity.User
import com.example.r_class.net.Connection
import com.example.r_class.utils.SPUtils
import com.example.r_class.view.main.MainActivity
import com.example.r_class.view.register.RegisterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : AppCompatActivity(),View.OnFocusChangeListener,TextWatcher,View.OnClickListener {

    private lateinit var loginUsername : EditText
    private lateinit var loginPassword : EditText
    private lateinit var loginButton : Button
    private lateinit var registerButton:Button
    private lateinit var markMe : CheckBox
    private lateinit var spUtils: SPUtils
    private lateinit var delUsername : ImageView
    private lateinit var delPassword : ImageView
   // private lateinit var loginDialog : ProgressDialog
    private lateinit var dialog : LoadingDialog
    private lateinit var userDao : UserDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initVariables()
        initView()
        initData()
    }

    private fun initData(){
        val username = loginUsername.text.toString().trim { it <= ' ' }
        val pwd = loginPassword.text.toString().trim { it <= ' ' }
        //登录按钮是否可用
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            loginButton.isClickable = true
            loginButton.setBackgroundResource(R.drawable.bg_login_submit)
            loginButton.setTextColor(resources.getColor(R.color.black))
        } else {
            loginButton.setBackgroundResource(R.drawable.bg_login_submit_lock)
            loginButton.isClickable = false
            loginButton.setTextColor(resources.getColor(R.color.account_lock_font_color))
        }

        if (spUtils.remember){
            loginUsername.setText(spUtils.userName)
            loginPassword.setText(spUtils.password)
            markMe.isChecked = true
        }



    }

    private fun initView(){
        title()
        loginUsername = findViewById(R.id.et_login_username)
        loginPassword = findViewById(R.id.et_login_pwd)
        loginButton = findViewById(R.id.bt_login_submit)
        registerButton = findViewById(R.id.bt_login_register)
        markMe = findViewById(R.id.markme)
        delUsername = findViewById(R.id.iv_login_username_del)
        delPassword = findViewById(R.id.iv_login_pwd_del)



        loginUsername.addTextChangedListener(this)
        loginPassword.addTextChangedListener(this)

        loginButton.setOnClickListener(this)
        delUsername.setOnClickListener(this)
        delPassword.setOnClickListener(this)
        registerButton.setOnClickListener(this)

    }




    private fun initVariables(){
        spUtils = SPUtils(this)
        dialog = LoadingDialog(this)
        userDao = UserDB.getDatabase(this).userDao()
    }

    private fun openDialog(){
        dialog.createDialog()
    }


    private fun closeloading() {
        dialog.closeDialog()
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        val username = loginUsername.text.toString().trim { it <= ' ' }
        val pwd = loginPassword.text.toString().trim { it <= ' ' }

        //是否显示清除按钮

        if (username.isNotEmpty()) {
            delUsername.visibility = View.VISIBLE
        } else {
            delUsername.visibility = View.INVISIBLE
        }
        if (pwd.isNotEmpty()) {
            delPassword.visibility = View.VISIBLE
        } else {
            delPassword.visibility = View.INVISIBLE
        }

        //登录按钮是否可用
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            loginButton.isClickable = true
            loginButton.setBackgroundResource(R.drawable.bg_login_submit)
            loginButton.setTextColor(resources.getColor(R.color.black))
        } else {
            loginButton.setBackgroundResource(R.drawable.bg_login_submit_lock)
            loginButton.isClickable = false
            loginButton.setTextColor(resources.getColor(R.color.account_lock_font_color))
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        val id = v?.id

        if (id == R.id.et_login_username) {
            if (hasFocus) {
                loginUsername.isActivated = true
                loginPassword.isActivated = false
            }
        } else {
            if (hasFocus) {
                loginPassword.isActivated = true
                loginUsername.isActivated = false
            }
        }
    }

    private fun loginRequest(){
        openDialog()
        val account = loginUsername.text.toString()
        val pass = loginPassword.text.toString()
        if (account == "" || pass == "") {
            closeloading()
            Toast.makeText(this@LoginActivity, "请输入学号或者密码", Toast.LENGTH_SHORT).show()
        }
        val user = userDao.queryWithIdentityNumber(account)
        if (user != null && pass == user.password){
            if (markMe.isChecked){
                spUtils.remember = true
            }
            spUtils.password = pass
            spUtils.userName = account
            spUtils.identify = user.identity
            spUtils.name = user.name
            Toast.makeText(this@LoginActivity, "登陆成功", Toast.LENGTH_SHORT).show()
            closeloading()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            var query_map = HashMap<String, String>()
            query_map["operation"] = "QueryUsers"
            query_map["identitycode"] = account
            query_map["pass"] = pass
            val query_json = JSONObject(query_map as Map<*, *>)
            CoroutineScope(Dispatchers.Main).launch {
                val str = Connection().get(query_json.toString())
                val json = JSONObject(str)
                val operation = json.get("operation")
                if (operation == "ResponseQuery") {
                    val found = json.get("found")
                    if (found == "false") {
                        closeloading()
                        Toast.makeText(this@LoginActivity, "从云端数据库获取登陆信息失败", Toast.LENGTH_SHORT).show()
                    } else {
                        userDao.insertUser(User(
                            phone = json.getString("phone"),
                            password = json.getString("password"),
                            name = json.getString("name"),
                            identity = json.getString("identity"),
                            identityNumber = json.getString("identitycode")))

                        if (markMe.isChecked){
                            spUtils.remember = true
                        }
                        spUtils.password = pass
                        spUtils.userName = account
                        spUtils.identify = json.getString("identity")
                        spUtils.name = json.getString("name")
                        closeloading()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                } else {
                    Toast.makeText(this@LoginActivity, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT).show()
                }

            }
        }



    }



    override fun onClick(v: View?) {
        v?.let {
            when(v.id){
                R.id.ib_navigation_back -> {
                }
                R.id.et_login_username -> {
                    loginPassword.clearFocus()
                    loginUsername.isFocusableInTouchMode = true
                    loginUsername.requestFocus()
                }
                R.id.et_login_pwd -> {
                    loginUsername.clearFocus()
                    loginPassword.isFocusableInTouchMode = true
                    loginPassword.requestFocus()
                }
                R.id.iv_login_username_del ->
                    //清空用户名
                    loginUsername.setText(null)
                R.id.iv_login_pwd_del ->
                    //清空密码
                    loginPassword.setText(null)
                R.id.bt_login_submit ->
                    //登录
                    loginRequest()
                R.id.bt_login_register -> {
                    //注册
                    startActivity(Intent(
                        this@LoginActivity, RegisterActivity::class.java))
                    finish()
                }
                R.id.tv_login_forget_pwd ->
                    //忘记密码
                TODO("忘记密码")
                    //startActivity(Intent(this@LoginActivity, ForgetPwdActivity::class.java))
                else -> {
                }
            }
        }
    }

}