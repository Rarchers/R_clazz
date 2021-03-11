package com.example.r_class.view.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.example.r_class.R
import com.example.r_class.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() , View.OnClickListener, TextWatcher {
    internal lateinit var phones: EditText
    internal lateinit var code: EditText
    internal lateinit var get: TextView
    internal lateinit var read: CheckBox
    internal lateinit var regist: Button
    internal lateinit var ll_register_phone: LinearLayout
    internal lateinit var ll_register_sms_code: LinearLayout
    internal var TAG = "R"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        init()
        val username = phones.text.toString().trim { it <= ' ' }
        val pwd = code.text.toString().trim { it <= ' ' }
        //登录按钮是否可用
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            regist.isClickable = true
            regist.setBackgroundResource(R.drawable.bg_login_submit)
            regist.setTextColor(resources.getColor(R.color.white))
        } else {
            regist.setBackgroundResource(R.drawable.bg_login_submit_lock)
            regist.isClickable = false
            regist.setTextColor(resources.getColor(R.color.account_lock_font_color))
        }
        findViewById<ImageButton>(R.id.ib_navigation_back).setOnClickListener(this)

        read.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                val phone = phones.text.toString().trim { it <= ' ' }
                val codes = code.text.toString().trim { it <= ' ' }
                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(codes)) {
                    regist.isClickable = true
                    regist.setBackgroundResource(R.drawable.bg_login_submit)
                    regist.setTextColor(resources.getColor(R.color.white))
                } else {
                    regist.isClickable = false
                    regist.setBackgroundResource(R.drawable.bg_login_submit_lock)
                    regist.setTextColor(resources.getColor(R.color.account_lock_font_color))
                }
            }
        }
    }
    override fun onClick(view: View) {
        when (view.id) {
            R.id.ib_navigation_back -> {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
            R.id.tv_register_sms_call -> {
                val pn = phones.text.toString()
                if (pn == "")
                    Toast.makeText(applicationContext, "请输入你的手机号来获取验证码", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(applicationContext, "您的验证码为0319", Toast.LENGTH_SHORT).show()
            }
            R.id.bt_register_submit -> {
                val pns = phones.text.toString()
                if (pns != "" && code.text.toString() == "0319"&&pns.length==11) {
                    val intent = Intent(this@RegisterActivity, Register_two::class.java)
                    intent.putExtra("phones", pns)
                    startActivity(intent)
                    finish()
                } else
                    Toast.makeText(applicationContext, "验证码或手机错误", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init() {
        phones = findViewById(R.id.et_register_username)
        code = findViewById(R.id.et_register_auth_code)
        get = findViewById(R.id.tv_register_sms_call)
        read = findViewById(R.id.cb_protocol)
        regist = findViewById(R.id.bt_register_submit)
        ll_register_phone = findViewById(R.id.ll_register_phone)
        ll_register_sms_code = findViewById(R.id.ll_register_sms_code)

        regist.setOnClickListener(this)
        get.setOnClickListener(this)
        phones.addTextChangedListener(this)
        code.addTextChangedListener(this)

    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        val phone = phones.text.toString().trim { it <= ' ' }
        val codes = code.text.toString().trim { it <= ' ' }

        Log.d(TAG, "afterTextChanged: 检查是否开放注册")
        //注册按钮是否可用
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(codes) && read.isChecked) {
            regist.isClickable = true
            regist.setBackgroundResource(R.drawable.bg_login_submit)
            regist.setTextColor(resources.getColor(R.color.white))
        } else {
            regist.isClickable = false
            regist.setBackgroundResource(R.drawable.bg_login_submit_lock)
            regist.setTextColor(resources.getColor(R.color.account_lock_font_color))
        }
    }

    //阅读服务条款事件
    override fun afterTextChanged(s: Editable) {


    }

    //对返回键进行监听
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}