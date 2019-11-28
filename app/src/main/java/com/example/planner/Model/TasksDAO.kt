package com.example.planner.Model

import androidx.room.*


@Dao
interface TasksDAO{

    @Query("SELECT * FROM task")
    fun getAll(): List<Task>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getById(id: Long): Task

    @Insert
    fun insert(employee: Task): Long

    @Update
    fun update(employee: Task)

    @Delete
    fun delete(employee: Task)
}