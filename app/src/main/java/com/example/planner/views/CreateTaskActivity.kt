package com.example.planner.views

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.planner.Model.Priority
import com.example.planner.Model.Task
import com.example.planner.Presenter.CreateTaskPresenter
import com.example.planner.R
import android.content.Intent
import android.widget.*
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.DialogFragment
import com.example.planner.Alarm.AlarmService
import com.jakewharton.rxbinding3.widget.textChangeEvents
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.milliseconds


class CreateTaskActivity : MvpAppCompatActivity(), CreateTaskView {
    override fun setTime(s: String) {
        deadlineTime.text = s
    }

    override fun setDate(s: String) {
        deadlineDate.text = s
    }

    override fun insertingComplete() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun insertingFailed() {
        Toast.makeText(this, "FAIL", Toast.LENGTH_SHORT).show()
    }

    lateinit var titleEdit: EditText
    lateinit var contentEdit: EditText
    lateinit var deadlineDate: TextView
    lateinit var deadlineTime: TextView
    lateinit var buttonSubmit: Button
    lateinit var radioGroup: RadioGroup
    lateinit var task: Task
    @InjectPresenter
    lateinit var presenter: CreateTaskPresenter
    var priority: Priority = Priority.GREEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getLongExtra("Id",-1)
        Timber.d("Oncreate.. id = $id")
        if(id > -1)
            presenter.setContent(id)
        setContentView(R.layout.activity_create_task)
        titleEdit = findViewById(R.id.editText_tile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        contentEdit = findViewById(R.id.editText_content)
        deadlineDate = findViewById(R.id.tv_date)
        deadlineDate.text = SimpleDateFormat("d.M.yyyy", Locale.getDefault()).format(System.currentTimeMillis())
        deadlineDate.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                Timber.d("deadline onClick")
                val dialog: DialogFragment = DeadlineDateDialogFragment(presenter, this@CreateTaskActivity)
                dialog.show(supportFragmentManager, "dlg1")
            }
        })
        deadlineTime = findViewById(R.id.tv_time)

        deadlineDate.text = SimpleDateFormat("d.M.yyyy", Locale.getDefault()).format(System.currentTimeMillis())
        deadlineTime.text = SimpleDateFormat("H:mm", Locale.getDefault()).format(System.currentTimeMillis())

        deadlineTime.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                Timber.d("deadlineTime onClick")
                val dialog: DialogFragment = DeadlineDialogFragment(presenter)
                    dialog.show(supportFragmentManager, "dlg1")
            }
        })

        buttonSubmit= findViewById(R.id.button_submit)
        buttonSubmit.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                Timber.d("buttonSubmit onClick")
                var sf = SimpleDateFormat("d.M.yyyy:H:m", Locale.getDefault())
                var s = sf.parse("${deadlineDate.text}:${deadlineTime.text}")
                Timber.d("buttonSubmit onClick..date = $s, title = ${titleEdit.text.toString()}")
                val newTask = Task(
                    title=titleEdit.text.toString(),
                    content = contentEdit.text.toString(),
                    priority = priority,
                    deadline = s.time)
                if(titleEdit.text.length!=0&&contentEdit.text.length!=0)
                    if(id>0) {
                        newTask.id = task.id
                        presenter.updateTask(newTask, this@CreateTaskActivity)
                    }
                else {
                    presenter.saveTask(newTask, this@CreateTaskActivity)
                }
            }

        })
        radioGroup = findViewById(R.id.radio_group_priority)
        radioGroup.setOnCheckedChangeListener(object :RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId) {
                R.id.radioButton_green -> priority = Priority.GREEN
                R.id.radioButton_red -> priority = Priority.RED
                R.id.radioButton_yellow -> priority = Priority.YELLOW

                }
            }

        })

    }

    override fun setContent(task: Task){
        Timber.d("setContent for task: ${task.title}")
        this.task = task
        titleEdit.setText(task.title)
        contentEdit.setText(task.content)
        deadlineDate.text = SimpleDateFormat("d.M.yyyy", Locale.getDefault()).format(task.deadline)
        deadlineTime.text = SimpleDateFormat("H:mm", Locale.getDefault()).format(task.deadline)
        when(task.priority){
            Priority.GREEN -> radioGroup.check(R.id.radioButton_green)
            Priority.YELLOW -> radioGroup.check(R.id.radioButton_yellow)
            Priority.RED -> radioGroup.check(R.id.radioButton_red)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
