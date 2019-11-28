package com.example.planner.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.planner.Adapter.MarginItemDecoration
import com.example.planner.Adapter.TasksAdapter
import com.example.planner.Model.Task
import com.example.planner.Presenter.TasksPresenter
import com.example.planner.R

import kotlinx.android.synthetic.main.activity_tasks.*
import timber.log.Timber

class TasksActivity : MvpAppCompatActivity(), TasksView {

    lateinit var adapter: TasksAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    @InjectPresenter
    lateinit var presenter: TasksPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("OnCreate")
        setContentView(R.layout.activity_tasks)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        progressBar = findViewById(R.id.progressBar)
        adapter = TasksAdapter(presenter)
        recyclerView = findViewById(R.id.rv_tasks)
        recyclerView.adapter = this.adapter
        recyclerView.addItemDecoration(
            MarginItemDecoration(
            resources.getDimension(R.dimen.default_padding).toInt())
        )
        val lm = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = lm
        recyclerView.setHasFixedSize(true)

        fab.setOnClickListener { view ->
            Timber.d("fab onClick")
            presenter.changeTask(-1)
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        presenter.loadTasks()
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun addTasks(tasks: ArrayList<Task>) {
        adapter.addTasks(tasks)
    }

    override fun changeTaskActivity(id: Long) {
        val intent = Intent(this, CreateTaskActivity::class.java)
        intent.putExtra("Id", id)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
