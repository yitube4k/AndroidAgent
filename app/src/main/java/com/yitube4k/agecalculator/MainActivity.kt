package com.yitube4k.agecalculator

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import java.util.Calendar

class MainActivity : Activity() {
    private var selectedDate: Calendar? = null
    private var birthdate: Calendar? = null
    private var textViewDate: TextView? = null
    private var textViewAge: TextView? = null
    private var textViewDays: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewDate = findViewById(R.id.textViewDate)
        textViewAge = findViewById(R.id.textViewAge)
        textViewDays = findViewById(R.id.textViewDays)

        val buttonDate = findViewById<View>(R.id.buttonDate) as android.widget.Button
        buttonDate.setOnClickListener { showDatePicker() }

        val calculateButton = findViewById<View>(R.id.calculateButton) as android.widget.Button
        calculateButton.setOnClickListener { calculateAge() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate = Calendar.getInstance()
                selectedDate?.set(year, month, day)
                birthdate = selectedDate
                updateViews()
            },
            year,
            month,
            day
        )
        datePicker.show()
    }

    private fun updateViews() {
        textViewDate?.text = getString(R.string.date_selected, selectedDate?.time.toString())
    }

    private fun calculateAge() {
        val bd = birthdate ?: return
        val currentDate = Calendar.getInstance()
        val age = calculateAgeInYears(bd, currentDate)

        val daysLived = calculateDaysLived(bd, currentDate)
        val daysUntilNextBirthday = calculateDaysUntilNextBirthday(bd, currentDate)

        val monthsTot = daysLived / 30
textViewAge?.text = getString(R.string.age_calculated, age, monthsTot % 12, daysLived % 30)
        textViewDays?.text = getString(R.string.days_lived, daysLived, daysUntilNextBirthday)
    }

    private fun calculateAgeInYears(birthdate: Calendar, currentDate: Calendar): Int {
        val age = currentDate.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR)
        if (currentDate.get(Calendar.MONTH) < birthdate.get(Calendar.MONTH) ||
            (currentDate.get(Calendar.MONTH) == birthdate.get(Calendar.MONTH) && currentDate.get(Calendar.DAY_OF_MONTH) < birthdate.get(Calendar.DAY_OF_MONTH))) {
            return age - 1
        }
        return age
    }

    private fun calculateDaysLived(birthdate: Calendar, currentDate: Calendar): Int {
        val date1 = birthdate.time
        val date2 = currentDate.time

        val difference = date2.time - date1.time
        return (difference / (24 * 60 * 60 * 1000)).toInt()
    }

    private fun calculateDaysUntilNextBirthday(birthdate: Calendar, currentDate: Calendar): Int {
        val next = Calendar.getInstance().apply {
            timeInMillis = birthdate.timeInMillis
        }
        val currentDateCopy = Calendar.getInstance().apply {
            timeInMillis = currentDate.timeInMillis
        }

        next.set(Calendar.YEAR, currentDateCopy.get(Calendar.YEAR))
        if (next.before(currentDateCopy)) {
            next.set(Calendar.YEAR, currentDateCopy.get(Calendar.YEAR) + 1)
        }
        val difference = next.timeInMillis - currentDateCopy.timeInMillis
        return (difference / (24 * 60 * 60 * 1000)).toInt()
    }
}