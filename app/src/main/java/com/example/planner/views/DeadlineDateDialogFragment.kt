package com.example.planner.views

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.planner.Presenter.CreateTaskPresenter

class DeadlineDateDialogFragment(val presenter: CreateTaskPresenter, val activityContext: Context) : DialogFragment(),
    DatePickerDialog.OnDateSetListener{

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dp = DatePickerDialog(activityContext, this, 2019, 10, 28)
        dp.datePicker.minDate = System.currentTimeMillis()-1000
        return dp
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
         presenter.setDate("$dayOfMonth.${month+1}.$year")
    }
}