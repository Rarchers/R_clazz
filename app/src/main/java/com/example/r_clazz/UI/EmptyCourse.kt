package com.example.r_clazz.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.r_clazz.R
import com.example.r_clazz.Service.EmptyService
import com.example.r_clazz.Service.Student_Service

class EmptyCourse : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_course)
        val intent = Intent(this, Student_Service::class.java)
        intent.putExtra("clazz_code","......")
        startService(intent)
        finish()
    }
}
