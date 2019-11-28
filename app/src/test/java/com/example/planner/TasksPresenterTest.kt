package com.example.planner

import com.example.planner.Model.Priority
import com.example.planner.Model.Task
import com.example.planner.Presenter.TasksPresenter
import com.example.planner.Provider.TasksProvider
import com.example.planner.views.TasksView
import com.example.planner.views.`TasksView$$State`
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import org.mockito.MockitoAnnotations
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import java.util.concurrent.TimeUnit
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import android.content.Context
import io.reactivex.Completable

class TasksPresenterTest {

    @Mock
    internal var taskView: TasksView? = null

    @Mock
    internal var taskViewState: `TasksView$$State`? = null

    @Mock
    internal var tasksProvider: TasksProvider? = null

    private lateinit var presenter: TasksPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = TasksPresenter()
        tasksProvider = mock(TasksProvider::class.java)
        presenter.taskProvider = tasksProvider!!
        presenter.attachView(taskView)
        presenter.setViewState(taskViewState)

            val immediate = object :Scheduler(){

                override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable{
                    return super.scheduleDirect(run, 0, unit)
                }
                override fun createWorker(): Worker {
                    return ExecutorScheduler.ExecutorWorker(Runnable::run, true)
                }

            }

        RxJavaPlugins.setInitIoSchedulerHandler({ t->immediate })
        RxJavaPlugins.setInitComputationSchedulerHandler({ t->immediate })
        RxJavaPlugins.setInitNewThreadSchedulerHandler({ t->immediate })
        RxJavaPlugins.setInitSingleSchedulerHandler({ t->immediate })
        RxAndroidPlugins.setInitMainThreadSchedulerHandler({ t->immediate })
    }

    @Test
    fun changeTask() {
        presenter.changeTask(0)
        verify(taskViewState)!!.changeTaskActivity(0)
    }

    @Test
    fun load_tasks_not_empty() {
        val list:ArrayList<Task> = ArrayList()
        list.add(Task("","",0,Priority.YELLOW))
        `when`(tasksProvider!!.loadTasks()).thenReturn(
            Single.create<ArrayList<Task>>({
                it.onSuccess(list)
            }))

        presenter.loadTasks()

        verify(taskViewState)!!.hideProgress()
        verify(taskViewState)!!.addTasks(list)
    }

    @Test
    fun load_tasks_error() {
        val list:ArrayList<Task> = ArrayList()
        list.add(Task("","",0,Priority.YELLOW))
        `when`(tasksProvider!!.loadTasks()).thenReturn(
            Single.create<ArrayList<Task>>({
                it.onError(Throwable())
            }))

        presenter.loadTasks()

        verify(taskViewState)!!.hideProgress()
        verify(taskViewState, never())!!.addTasks(list)
    }

    @Test
    fun complete_task() {
        val task = Task("","",0,Priority.YELLOW)
        task.id = Integer(1)

        `when`(tasksProvider!!.deleteTask(task)).thenReturn(
            Completable.create({
                it.onError(Throwable())
            }))

        presenter.completeTask(task, mock(Context::class.java))
        verify(taskViewState, never())!!.hideProgress()
    }
}