package com.bliszkot.medicinealarm

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.getBroadcast
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import java.util.*

class AlarmActivity : AppCompatActivity() {
    private lateinit var medicineText: EditText
    private lateinit var dateText: EditText
    private lateinit var timeText: EditText
    private lateinit var insertButton: Button
    private var medicine = ""
    private var date = ""
    private var time = ""
    private var hourMain = 0
    private var minMain = 0
    private var amPm = ""
    private var yearMain = 0
    private var monthMain = 0
    private var dateMain = 0
    private var intentArray = ArrayList<PendingIntent>()
    private lateinit var alarmMgr: AlarmManager
    private lateinit var alarmIntent: PendingIntent
    private lateinit var alarmReceiverIntent: Intent

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        medicineText = findViewById(R.id.medicineNameText)
        dateText = findViewById(R.id.dateText)
        timeText = findViewById(R.id.timeText)
        insertButton = findViewById(R.id.insertButton)
        dateText.setOnClickListener { clickDatePicker() }
        timeText.setOnClickListener { clickTimePicker() }

        insertButton.setOnClickListener {
            medicine = medicineText.text.toString()
            date = dateText.text.toString()
            time = timeText.text.toString()

            if (medicine != "" && date != "" && time != "") {
                alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.DATE, dateMain)
                    set(Calendar.MONTH, monthMain)
                    set(Calendar.YEAR, yearMain)
                    set(Calendar.HOUR, hourMain)
                    set(Calendar.MINUTE, minMain)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    val amOrPm: Int = if (amPm == "AM") 0 else 1
                    set(Calendar.AM_PM, amOrPm)
                }

                val requestCode = System.currentTimeMillis().toInt()

                alarmReceiverIntent = Intent(this, AlarmReceiver::class.java).apply {
                    putExtra("extra", "play")
                    putExtra("resCode", requestCode)
                }
                alarmIntent = getBroadcast(this, requestCode, alarmReceiverIntent, FLAG_IMMUTABLE)

                alarmMgr.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    alarmIntent
                )

                intentArray.add(alarmIntent)

                try {
                    val dbHelper = DbHelper(this)
                    val db = dbHelper.writableDatabase

                    val values = ContentValues().apply {
                        put(DbHelper.FeedEntry.COLUMN_NAME_MEDICINE_NAME, medicine)
                        put(DbHelper.FeedEntry.COLUMN_NAME_RES_ID, ""+requestCode)
                        put(DbHelper.FeedEntry.COLUMN_NAME_DATE, date)
                        put(DbHelper.FeedEntry.COLUMN_NAME_TIME, time)
                    }

                    val num: Long = db.insert(DbHelper.FeedEntry.TABLE_NAME, null, values)
                    if (num != -1L) {
                        Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(this)
                        }
                    } else {
                        Log.e(TAG, "Error: $num: ")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error: $e")
                }
            } else {
                Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clickTimePicker() {
        val myCalendar = Calendar.getInstance()
        val hour = myCalendar[Calendar.HOUR]
        val mins = myCalendar[Calendar.MINUTE]

        TimePickerDialog(this, { _, i, i2 ->
            val morningOrAfternoon: String
            var hours = i
            if (i in 12..23) {
                morningOrAfternoon = "PM"
                if (i != 12) hours = i - 12
            } else {
                if (hours == 0) hours = 12
                morningOrAfternoon = "AM"
            }

            val minutes = if ((i2 / 10) == 0) {
                "0$i2"
            } else {
                i2.toString()
            }

            hourMain = hours
            minMain = minutes.toInt()
            amPm = morningOrAfternoon
            val time = "$hours:$minutes$morningOrAfternoon"
            timeText.setText(time)
        }, hour, mins, false).show()
    }

    private fun clickDatePicker() {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar[Calendar.YEAR]
        val month = myCalendar[Calendar.MONTH]
        val day = myCalendar[Calendar.DAY_OF_MONTH]
        DatePickerDialog(
            this,
            { _, year1, monthOfYear, dayOfMonth ->
                val monthD = monthOfYear + 1
                val date = "$dayOfMonth/$monthD/$year1"

                dateMain = dayOfMonth
                monthMain = monthOfYear
                yearMain = year1
                dateText.setText(date)
            },
            year,
            month,
            day
        ).show()
    }
}