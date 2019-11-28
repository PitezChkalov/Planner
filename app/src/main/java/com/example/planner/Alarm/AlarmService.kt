package com.example.planner.Alarm

import androidx.core.app.NotificationManagerCompat
import android.content.Intent
import android.content.Context
import android.os.IBinder
import com.example.planner.R
import com.example.planner.views.TasksActivity
import androidx.core.app.NotificationCompat
import android.app.*
import android.graphics.Color
import android.media.RingtoneManager
import com.example.planner.Model.Task
import com.example.planner.Provider.TasksProvider
import timber.log.Timber
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class AlarmService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var title: String
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            Timber.d("onStartCommand action = ${intent.action}")
            when (intent.action) {
                "android.intent.action.BOOT_COMPLETED" -> {
                    val provider = TasksProvider()
                    provider.loadTasks().observeOn(AndroidSchedulers.mainThread()).subscribeOn(
                        Schedulers.io()
                    )
                        .subscribe(object : SingleObserver<ArrayList<Task>> {
                            override fun onSuccess(t: ArrayList<Task>) {
                                loadingComplete(t)
                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onError(e: Throwable) {
                                Timber.e(e)
                            }

                        })
                    return START_STICKY
                }
                "com.example.planner.alarm" -> {
                    title = intent.getStringExtra("title")
                    Timber.d("title = $title, id = ${intent.getIntExtra("id", 0)}")
                    createNotification(intent.getIntExtra("id", 0))
                }
                else -> stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate..creating notification")
        val notification = NotificationCompat.Builder(this)
            .setContentTitle(resources.getString(R.string.app_name))
            .setTicker(resources.getString(R.string.app_name))
            .setContentText(resources.getString(R.string.push))
            .setSmallIcon(R.drawable.ic_add)
            .setOngoing(true)

            .build()
        notification.flags =
            notification.flags or Notification.FLAG_NO_CLEAR     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        startForeground(NOTIFICATION_FOREGROUND_ID, notification)
    }

    fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Timber.d("Creating notification channel")
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                NOTIFICATION_CHANEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = resources.getString(R.string.push)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(id: Int) {
        Timber.d("Creating new notification")
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANEL_ID)
        builder.setSmallIcon(R.drawable.ic_launcher_background)
        builder.setContentTitle(title)
        builder.setContentText(resources.getText(R.string.deadline))
        builder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        builder.setSound(uri)
        val notifyIntent = Intent(this, TasksActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent)
        val notificationCompat = builder.build()
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(id, notificationCompat)
    }

    private fun setAlarm(task: Task, context: Context) {
        if (task.deadline - System.currentTimeMillis() > 0) {
            Timber.d("send new notification broadcast for task ${task.title}")
            val notifyIntent = Intent("com.example.planner.alarm")
            notifyIntent.putExtra("title", task.title)
            notifyIntent.putExtra("id", task.id)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                notifyIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            var alarmTime = task.deadline - (1000 * 60 * 60)
            Timber.d("alarm Time =  $alarmTime")

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
        else
            Timber.e("deadline < current time")
    }

    companion object {
        private val NOTIFICATION_FOREGROUND_ID = 2
        private val NOTIFICATION_ID = 3
        private val NOTIFICATION_CHANEL_ID = "1"
        private val NOTIFICATION_CHANNEL_NAME = "Planner push"
    }

    fun loadingComplete(tasks: ArrayList<Task>) {
        Timber.d("Loading complete...Tasks size = ${tasks.size}")
        for (task in tasks) {
            setAlarm(task, this)
        }
    }
}