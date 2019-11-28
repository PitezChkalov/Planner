package com.example.planner.Model

import androidx.room.TypeConverter
import com.example.planner.R

enum class Priority(val color: Int){
    RED(R.color.red), GREEN(R.color.green), YELLOW(R.color.yellow);

    override fun toString(): String {
        return name
    }

    class PriorityConverter{
        @TypeConverter
        fun fromPriority(priority: Priority):String{
            return priority.name
        }
        @TypeConverter
        fun toPriority(data: String):Priority{
            return Priority.valueOf(data)
        }
    }


}