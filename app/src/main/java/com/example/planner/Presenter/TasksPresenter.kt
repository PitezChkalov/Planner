package com.example.planner.Presenter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.planner.Model.Task
import com.example.planner.Provider.TasksProvider
import com.example.planner.views.TasksView
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


@InjectViewState
class TasksPresenter : MvpPresenter<TasksView>() {

    var taskProvider = TasksProvider()
    val disposables = CompositeDisposable()

    fun loadTasks() {
        Timber.d("Load tasks...")
        viewState.showProgress()
        taskProvider.loadTasks().observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<ArrayList<Task>> {
                override fun onSuccess(t: ArrayList<Task>) {
                    Timber.d("Load tasks complete")
                    viewState.hideProgress()
                    t.reverse()
                    viewState.addTasks(t)
                }

                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onError(e: Throwable) {
                    viewState.hideProgress()
                    Timber.e(e)
                }

            })
    }

    fun completeTask(task: Task, context: Context) {
        Timber.d("Complete tasks...")
        taskProvider.deleteTask(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    Timber.d("task completed")
                    loadTasks()
                    cancelNotification(task, context)
                }

                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }
            }
            )
    }

    fun cancelNotification(task: Task, context: Context) {
        Timber.d("cancel Notification broadcast for task: ${task.title}, deadline = ${task.deadline}")
        val notifyIntent = Intent("com.example.planner.alarm")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            notifyIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent);
    }

    fun changeTask(id: Long) {
        Timber.d("ChangeTask tasks...")
        viewState.changeTaskActivity(id)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
        disposables.dispose()
    }
}