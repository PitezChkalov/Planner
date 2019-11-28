package com.example.planner.Presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.planner.Model.Task
import com.example.planner.Provider.TasksProvider
import com.example.planner.views.CreateTaskView
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.planner.Alarm.AlarmReceiver
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
class CreateTaskPresenter : MvpPresenter<CreateTaskView>() {

    val tasksProvider: TasksProvider = TasksProvider()
    val disposables = CompositeDisposable()

    fun saveTask(task: Task, context: Context) {
        Timber.d("saveTask title = ${task.title}")
        tasksProvider.addTaskToDao(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Long> {
                override fun onSuccess(t: Long) {
                    Timber.d("saveTask Complete")
                    task.id = t.toInt() as Integer
                    createNotification(task, context = context)
                    viewState.insertingComplete()
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

    fun updateTask(task: Task, context: Context) {
        Timber.d("updateTask title = ${task.title}")
        tasksProvider.updateTaskToDao(task).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    Timber.d("updateTask Complete")
                    viewState.insertingComplete()
                }

                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }
            }
            )
        createNotification(task, context = context)
    }

    fun setContent(id: Long) {
        Timber.d("setContent id = $id")
        tasksProvider.getById(id).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<Task> {
                override fun onSuccess(t: Task) {
                    Timber.d("Loading complete for task ${t.title}")
                    viewState.setContent(t)
                }

                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }

            })
    }

    fun setTime(time: String) {
        Timber.d("setTime time = $time")
        viewState.setTime(time)
    }

    fun setDate(date: String) {
        Timber.d("setTime date = $date")
        viewState.setDate(date)
    }

    fun createNotification(task: Task, context: Context) {
        if (task.deadline - System.currentTimeMillis() > 0) {
            Timber.d("create Notification broadcast for task: ${task.title}, deadline = ${task.deadline}")
            val notifyIntent = Intent("com.example.planner.alarm")
            notifyIntent.putExtra("title", task.title)
            try {
                notifyIntent.putExtra("id", task.id)
            } catch (e: UninitializedPropertyAccessException) {
                notifyIntent.putExtra("id", 0)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                task.id.toInt(),
                notifyIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            val alarmTime = task.deadline - (1000 * 60*60)
            Timber.d("alarm Time =  $alarmTime")
            Timber.d(System.currentTimeMillis().toString())
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        }
        else
            Timber.e("deadline < current time")
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}