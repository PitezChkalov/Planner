package com.example.planner.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planner.Model.Task
import com.example.planner.Presenter.TasksPresenter
import com.example.planner.R
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.ContextMenu
import androidx.core.content.ContextCompat
import com.example.planner.Model.Priority
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class TasksAdapter(var presenter: TasksPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var tasks: ArrayList<Task> = ArrayList()
    init {
        startUpdating()
    }
    fun addTasks(tasks: ArrayList<Task>){
        this.tasks.clear()
        this.tasks.addAll(tasks)
        notifyDataSetChanged()
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_tasks, parent, false)
        context = parent.context
        return DetailsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    private fun startUpdating(){
        val handler = Handler()
        val runnable = object : Runnable{
            override fun run() {
                notifyDataSetChanged()
                handler.postDelayed(this, 1000*60)
            }
        }
        handler.post(runnable)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DetailsViewHolder)
            holder.bind(tasks[position], holder.itemView.context)
        holder.itemView.setOnLongClickListener({
            presenter.completeTask(tasks.get(position), holder.itemView.context)
            true
        })
        holder.itemView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                presenter.changeTask(tasks.get(position).id.toLong())
            }

        })
    }

    class DetailsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewTitle = itemView.findViewById<TextView>(R.id.rv_tasks_title)
        private val textViewContent = itemView.findViewById<TextView>(R.id.rv_tasks_content)
        private val textViewDeadLine = itemView.findViewById<TextView>(R.id.rv_tasks_deadline)
        private val priority = itemView.findViewById<ImageView>(R.id.rv_priority)

        fun bind(content: Task, context: Context) {
            textViewContent.text = content.content
            textViewTitle.text = content.title

            val diff =   content.deadline - System.currentTimeMillis()
            val diffHour = diff / (60 * 60 * 1000)
            val diffMinutes = diff / (60 * 1000) - diffHour*60
            when{
                diffHour == 0L -> textViewDeadLine.setTextColor(ContextCompat.getColor(context, Priority.RED.color))
                diffHour in 0..1 -> textViewDeadLine.setTextColor(ContextCompat.getColor(context, Priority.YELLOW.color))
                diffHour < 0L -> textViewDeadLine.setTextColor(ContextCompat.getColor(context, Priority.RED.color))
                else -> textViewDeadLine.setTextColor(ContextCompat.getColor(context, Priority.GREEN.color))
            }
            if(diff>0)
                    textViewDeadLine.text = "${diffHour}h:${diffMinutes}m"
            else
                textViewDeadLine.text = ""
            val color = ColorDrawable(ContextCompat.getColor(context, content.priority.color))
            priority.setImageDrawable(color)
        }


    }

}