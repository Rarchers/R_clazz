package com.example.r_class.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    val identity : String, //身份： 教师/学生
    val phone : String,
    val password : String,
    @PrimaryKey val identityNumber : String,   //教师号/学号
    val name : String
)

