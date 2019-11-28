package com.example.planner.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity
@TypeConverters(Priority.PriorityConverter::class)
data class Task(val title: String, val content: String, val deadline: Long, val priority: Priority){
    @PrimaryKey(autoGenerate = true)
    lateinit var id: Integer
}