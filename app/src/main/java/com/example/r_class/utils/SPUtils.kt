package com.example.r_class.utils


import android.content.Context


class SPUtils(context: Context) {

    var userName by Preference(context,"username", "")
    var password by Preference(context,"password","")
    var remember by Preference(context,"remember",false)
    var identify by Preference(context,"identify","教师")
    var name by Preference(context,"name","null")
}


