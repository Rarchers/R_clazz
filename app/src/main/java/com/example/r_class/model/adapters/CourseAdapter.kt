package com.example.r_class.model.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.r_class.R
import com.example.r_class.model.entity.Course

class CourseAdapter(val context: Context,val data:ArrayList<Course>) : BaseAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
       var view : View
       var holder : CourseHolder

       if (convertView == null){
           view = LayoutInflater.from(context).inflate(R.layout.clazz_item,null)
           holder = CourseHolder()
           holder.courseName = view.findViewById(R.id.course_title)
           holder.courseCode = view.findViewById(R.id.codings)
           view.tag = holder
       }else{
           view = convertView
           holder = convertView.tag as CourseHolder
       }

        val item = getItem(position) as Course

        holder.courseCode?.text = item.course_code
        holder.courseName?.text = item.course_name

        return view
    }





}