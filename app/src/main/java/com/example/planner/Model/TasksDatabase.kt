package com.example.planner.Model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1)
abstract class TasksDatabase: RoomDatabase(){
    abstract val tasksDao: TasksDAO
}