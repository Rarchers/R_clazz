package com.example.r_clazz.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.r_clazz.R

class CrouseForTeacher : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crouse_for_teacher)
        val code = intent.getStringExtra("course_code")

    }



}
