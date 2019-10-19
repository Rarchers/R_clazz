package com.example.r_clazz.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}
