package com.example.r_class.dialogs

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.airbnb.lottie.LottieAnimationView
import com.example.r_class.R

class LoadingDialog(val context: Context)  {

    lateinit var dialog: AlertDialog
    fun createDialog(){
        val view = LayoutInflater.from(context).inflate(R.layout.loading_view,null)
        val image = view.findViewById<LottieAnimationView>(R.id.imageView5)
        image.playAnimation()
        image.repeatCount = ValueAnimator.INFINITE

        dialog = AlertDialog.Builder(context)
            .setView(view)
            //.setCancelable(false)
            .create()
        dialog.show()
    }

    fun closeDialog(){
        if (dialog!=null && dialog.isShowing)
            dialog.cancel()
    }


}