package com.example.r_class.utils

import android.content.Context
import android.util.Log
import kotlin.reflect.KProperty

class Preference<T>(context: Context,val name:String,private val default: T)  {
    private val sp by lazy { context.getSharedPreferences(name,Context.MODE_PRIVATE) }

    operator fun getValue(thisRef: Any?, property: KProperty<*>):T{
        Log.e("info", "调用$this 的getValue()")
        return getSharedPreference(name,default)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>,value: T){
        Log.e("info", "调用$this 的setValue() value参数值为：$value")
        setSharedPreference(name,value)
    }

    private fun setSharedPreference(name: String,value: T) = with(sp.edit()){
        when(value){
            is Int -> putInt(name,value)
            is Long -> putLong(name,value)
            is Boolean -> putBoolean(name,value)
            is String -> putString(name,value)
            is Float -> putFloat(name,value)
            else -> throw IllegalArgumentException("This type of data cannot be saved!")
        }
    }.apply()



    private fun getSharedPreference(name: String,default: T) : T = with(sp){
        val res : Any = when(default){
            is Int -> getInt(name,default)
            is Long -> getLong(name,default)
            is Boolean -> getBoolean(name,default)
            is String -> getString(name,default)?:""
            is Float -> getFloat(name,default)
            else -> throw IllegalArgumentException("This type of data cannot be saved!")
        }
        return res as T
    }
}