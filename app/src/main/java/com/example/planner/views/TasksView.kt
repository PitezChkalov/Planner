package com.example.planner.views

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.planner.Model.Task

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface TasksView: MvpView {
    fun showProgress()
    fun hideProgress()
    fun addTasks(tasks: ArrayList<Task>)
    @StateStrategyType(value = SkipStrategy::class)
    fun changeTaskActivity(id:Long)
}