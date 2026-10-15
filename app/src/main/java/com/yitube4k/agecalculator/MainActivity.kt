package com.yitube4k.agecalculator

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var textViewDate: TextView
    private lateinit var textViewAge: TextView
    private lateinit var textViewDays: TextView
    private lateinit var buttonDate: Button
    private lateinit var calculateButton: Button

    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewDate = findViewById(R.id.textViewDate)
        textViewAge = findViewById(R.id.textViewAge)
        textViewDays = findViewById(R.id.textViewDays)
        buttonDate = findViewById(R.id.buttonDate)
        calculateButton = findViewById(R.id.calculateButton)

        buttonDate.setOnClickListener {
            showDatePicker()
        }

        calculateButton.setOnClickListener {
            calculateAge()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        selectedDate?.let {
            calendar.time = it
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val birthCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                selectedDate = birthCal.time
                updateViews()
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateViews() {
        selectedDate?.let {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            textViewDate.text = formatter.format(it)
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun calculateAge() {
        val birthDate = selectedDate ?: return

        val birthCal = Calendar.getInstance().apply {
            time = birthDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (birthCal.after(todayCal)) {
            return
        }

        val birthYear = birthCal.get(Calendar.YEAR)
        val birthMonth = birthCal.get(Calendar.MONTH)
        val birthDay = birthCal.get(Calendar.DAY_OF_MONTH)

        var years = todayCal.get(Calendar.YEAR) - birthYear
        val testCal = Calendar.getInstance().apply {
            timeInMillis = birthCal.timeInMillis
            add(Calendar.YEAR, years)
        }
        if (testCal.after(todayCal)) {
            years--
        }

        val walkerCal = Calendar.getInstance().apply {
            timeInMillis = birthCal.timeInMillis
            add(Calendar.YEAR, years)
        }

        var months = 0
        while (true) {
            val nextMonthCal = walkerCal.clone() as Calendar
            nextMonthCal.add(Calendar.MONTH, 1)
            if (!nextMonthCal.after(todayCal)) {
                walkerCal.add(Calendar.MONTH, 1)
                months++
            } else {
                break
            }
        }

        val remainingMillis = todayCal.timeInMillis - walkerCal.timeInMillis
        val days = (remainingMillis / (1000 * 60 * 60 * 24)).toInt()

        val totalDiffMillis = todayCal.timeInMillis - birthCal.timeInMillis
        val totalDaysLived = (totalDiffMillis / (1000 * 60 * 60 * 24)).toInt()

        val currentYear = todayCal.get(Calendar.YEAR)
        var nextBirthdayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        fun applyBirthdayDate(targetCal: Calendar, targetYear: Int) {
            if (birthMonth == Calendar.FEBRUARY && birthDay == 29) {
                if (isLeapYear(targetYear)) {
                    targetCal.set(targetYear, Calendar.FEBRUARY, 29)
                } else {
                    targetCal.set(targetYear, Calendar.MARCH, 1)
                }
            } else {
                targetCal.set(targetYear, birthMonth, birthDay)
            }
        }

        applyBirthdayDate(nextBirthdayCal, currentYear)

        if (nextBirthdayCal.before(todayCal)) {
            applyBirthdayDate(nextBirthdayCal, currentYear + 1)
        }

        val nextBirthdayMillis = nextBirthdayCal.timeInMillis - todayCal.timeInMillis
        val daysUntilNextBirthday = (nextBirthdayMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)

        textViewAge.text = getString(R.string.age_calculated, years, months, days)
        textViewDays.text = getString(R.string.days_lived, totalDaysLived, daysUntilNextBirthday)
    }
}
