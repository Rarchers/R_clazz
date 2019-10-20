package com.example.r_clazz.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import com.example.r_clazz.R

import com.example.r_clazz.Service.Student_Service





class CourseForStudent : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_for_student)
        val intent = Intent(this, Student_Service::class.java)
        intent.putExtra("clazz_code", getIntent().getStringExtra("course_code"))
        startService(intent)

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
            Toast.makeText(this, "再按一次退出课程", Toast.LENGTH_SHORT).show()
            mExitTime = System.currentTimeMillis()
        } else {
            Student_Service.stop()
            val intent = Intent(this, EmptyCourse::class.java)
            startActivity(intent)
            finish()
        }
    }
}
