package com.example.r_clazz.Receiver

import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.app.admin.DeviceAdminReceiver
import android.util.Log


class AdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d(TAG, "onEnabled")
        Toast.makeText(context, "激活成功", Toast.LENGTH_SHORT).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.d(TAG, "onDisabled")
        Toast.makeText(context, "取消激活",  Toast.LENGTH_SHORT).show()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(TAG, "onReceive")
    }

    companion object {
        private val TAG = "AdminReceiver"
    }
}

