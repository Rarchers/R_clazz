package com.example.r_class.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.r_class.model.daos.UserDao
import com.example.r_class.model.entity.User


@Database(entities = [User::class],version = 1)
abstract class UserDB : RoomDatabase(){
    companion object{
        fun getDatabase(context: Context) = Room.databaseBuilder(
            context,
            UserDB::class.java,
            "user.db"
        ).allowMainThreadQueries()
            .build()
    }

    abstract fun userDao() : UserDao
}