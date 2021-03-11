package com.example.r_class.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.r_class.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var navigationView: BottomNavigationView
    lateinit var navController : NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationView = findViewById(R.id.bottomNavigationView)
        navController = findNavController(R.id.nav_controller_view_tag)
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        NavigationUI.setupWithNavController(navigationView,navController)


    }
}