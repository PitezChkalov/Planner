package com.example.planner.Alarm

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import com.example.planner.R
import timber.log.Timber


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, arg1: Intent) {

        Timber.d("boot_broadcast. starting service...")
        val intent = Intent(context, AlarmService::class.java)
        if(arg1.getStringExtra("title")!=null) {
            intent.putExtra("title", arg1.getStringExtra("title"))
        }else
            intent.putExtra("title", context.resources.getString(R.string.app_name))
        intent.putExtra("id", arg1.getIntExtra("id",0))
        intent.action = arg1.action
        context.startService(intent)
    }

}