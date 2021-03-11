package com.example.r_class.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.r_class.model.entity.Course


class CourseViewModel : ViewModel() {
    private val _course : MutableLiveData<ArrayList<Course>> by lazy { MutableLiveData<ArrayList<Course>>().also { it.value = ArrayList() } }

    val course : LiveData<ArrayList<Course>>
    get() = _course


    fun freshCourse(){
        for (i in 0 until _course.value?.size!!){
            _course.value?.remove(_course.value!![0])
        }

    }

    fun addCourse(course: Course){
        _course.value?.add(course)
    }

}