package com.example.planner.views

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.planner.Presenter.CreateTaskPresenter
import com.example.planner.R

class DeadlineDialogFragment(val presenter: CreateTaskPresenter) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return TimePickerDialog(activity, this, 14, 35, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        presenter.setTime("$hourOfDay:$minute")
    }

}