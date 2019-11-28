package com.example.planner.Provider

import com.example.planner.Application
import com.example.planner.Model.Task
import com.example.planner.Model.TasksDAO
import com.example.planner.Model.TasksDatabase
import io.reactivex.*
import java.lang.Exception
import kotlin.collections.ArrayList

class TasksProvider(){

    val database: TasksDatabase? = Application.getDatabase()
    lateinit var tasksDao: TasksDAO

    init {
        if(database!=null)
        tasksDao = database.tasksDao
    }

    fun addTaskToDao(task: Task): Single<Long>{
       return Single.create<Long> {
            try {
                it.onSuccess(tasksDao.insert(task))
            }
            catch (e: Exception){
                it.onError(e)
            }
        }
    }

    fun updateTaskToDao(task: Task): Completable{
        return Completable.create {
            try {
                tasksDao.update(task)
                it.onComplete()
            }
            catch (e: Exception){
                it.onError(e)
            }
        }
    }

    fun deleteTask(task: Task):Completable{
       return Completable.create {
            try {
                tasksDao.delete(task)
                it.onComplete()
            }
            catch (e: Exception){
                it.onError(e)
            }
        }
    }

    fun loadTasks():Single<ArrayList<Task>>{
       return Single.create<ArrayList<Task>> {
            it.onSuccess(ArrayList(tasksDao.getAll()))
        }
    }

    fun getById(id: Long): Single<Task>{
       return Single.create<Task> {
            it.onSuccess(tasksDao.getById(id))
        }
    }
}