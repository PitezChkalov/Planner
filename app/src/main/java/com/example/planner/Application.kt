package com.example.planner

import android.content.Context
import android.content.IntentFilter
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.planner.Alarm.AlarmReceiver
import com.example.planner.Model.TasksDatabase
import com.example.planner.logging.DebugTree
import com.example.planner.logging.FileTree
import timber.log.Timber

class Application : android.app.Application(){
    companion object{
        private lateinit var database: TasksDatabase
        fun initDatabase(context: Context){
            database=
                Room.databaseBuilder(context, TasksDatabase::class.java, "database").build()
        }
        fun getDatabase(): TasksDatabase?{
            if(::database.isInitialized)
            return database
            else return null
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        Timber.plant(FileTree())
        initDatabase(this)
        val filter = IntentFilter("com.example.planner.alarm")
        this.registerReceiver(AlarmReceiver(), filter)
    }
}
