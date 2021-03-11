package com.example.r_class.view.joinclass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.r_class.R
import com.example.r_class.net.Connection
import com.example.r_class.ui.VerificationCodeView
import com.example.r_class.utils.SPUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class JoinClazz : AppCompatActivity(), VerificationCodeView.OnCodeFinishListener {

    lateinit var connection: Connection
    private var verificationcodeview: VerificationCodeView? = null
    lateinit var spUtils: SPUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_clazz)
        verificationcodeview = findViewById(R.id.verificationcodeview)
        verificationcodeview?.onCodeFinishListener = this
        connection = Connection()
        spUtils = SPUtils(this)
    }

    override fun onTextChange(view: View?, content: String?) {

    }

    override fun onComplete(view: View?, content: String?) {
        if (view == verificationcodeview) {
            //Toast.makeText(this, "输入完成：" + content, Toast.LENGTH_SHORT).show()
                var query_map = HashMap<String, String>()
                query_map.put("operation", "\"JoinClazz\"")
                query_map.put("clazz_code", "\"${content.toString()}\"")
                query_map.put("stucode","\"${spUtils.userName}\"")
                val query_json = JSONObject(query_map as Map<*, *>)
                print(query_json)
                CoroutineScope(Dispatchers.IO).launch {
                    val result = connection.get(query_map.toString())
                    val json = JSONObject(result)
                    val opreation = json.get("operation")
                    if (opreation == "ResponseJoin"){
                        val success = json.getString("success")
                        CoroutineScope(Dispatchers.Main).launch {
                            if (success=="true"){
                                Toast.makeText(this@JoinClazz,"加入成功，请返回后重新刷新页面", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this@JoinClazz,"请检查您的加课码是否正确", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(this@JoinClazz, "未知错误，请联系开发者获得更多支持", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }
    }
}