package com.example.planner.views

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.planner.Model.Task

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface CreateTaskView: MvpView {
    fun insertingComplete()
    fun setContent(task: Task)
    fun insertingFailed()
    fun setTime(s: String)
    fun setDate(s: String)
}