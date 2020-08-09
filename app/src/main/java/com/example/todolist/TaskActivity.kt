package com.example.todolist

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


const val DB_NAME="todoDB"
class TaskActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var myCalender: Calendar
    private val labels= arrayListOf("Personal", "Business", "Insurance", "Shopping")
    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener
    var finalDate = 0L
    var finalTime = 0L

    val db by lazy {
        AppDataBase.getDatabase(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        setUpSpinner()
        dateEdit.setOnClickListener(this)
        timeEdit.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        
    }

    private fun setUpSpinner() {
        val adapter=ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            labels
        )
        labels.sort()
        spinnerCategory.adapter=adapter
    }

    override fun onClick(p0: View) {
        if(p0.id==R.id.dateEdit)
            setListener()
        if(p0.id==R.id.timeEdit)
            setTimeListener()
        if(p0.id==R.id.saveBtn) {
            saveTodo()
        }
    }

    private fun setTimeListener() {
        myCalender= Calendar.getInstance()
        timeSetListener=TimePickerDialog.OnTimeSetListener { timePicker, hourOfday, min ->
            myCalender.set(Calendar.HOUR_OF_DAY, hourOfday)
            myCalender.set(Calendar.MINUTE, min)
            updateTime()
        }
        val timepickerdialog=TimePickerDialog(
            this,
            timeSetListener,
            myCalender.get(Calendar.HOUR_OF_DAY),
            myCalender.get(Calendar.MINUTE),
            false
        )


        timepickerdialog.show()
    }

    private fun updateTime() {
        //2:30 am
        val myFormat="hh:mm a"
        val sdf=SimpleDateFormat(myFormat)
        finalTime=myCalender.time.time
        timeEdit.setText(sdf.format(myCalender.time))

    }

    private fun setListener() {
        myCalender= Calendar.getInstance()
        dateSetListener=DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            myCalender.set(year, month, day)
            //need to show the date in the date box
            updateDate()
        }

        val datePickerDialog=DatePickerDialog(
            this,
            dateSetListener,
            myCalender.get(Calendar.YEAR),
            myCalender.get(Calendar.MONTH),
            myCalender.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate=System.currentTimeMillis()
       datePickerDialog.show()
    }

    private fun updateDate() {
        //Mon, 5 Jan 2020
        val myFormat="EEE, d MMM YYYY"
        val sdf=SimpleDateFormat(myFormat)
        finalDate=myCalender.time.time
        dateEdit.setText(sdf.format(myCalender.time))
        //this will make the time box visible
        TimeInpLayout.visibility=View.VISIBLE
    }

    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = TitleInpLayout.editText?.text.toString()
        val description = TaskInpLayout.editText?.text.toString()

        GlobalScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
                return@withContext db.tododao().insert(
                    ToDoModel(
                        title,
                        description,
                        category,
                        finalDate,
                        finalTime
                    )
                )
            }
            finish()
        }

    }

}