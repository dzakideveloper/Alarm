package com.example.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.nanda.AlarmService
import com.nanda.TAG_TIME_PICKER
import com.nanda.alarm.databinding.ActivityRepeatingAlarmBinding
import com.nanda.data.Alarm
import com.nanda.data.local.AlarmDB
import com.nanda.data.local.AlarmDao
import com.nanda.fragment.TimePickerFragment
import kotlinx.android.synthetic.main.activity_one_time_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RepeatingAlarmActivity : AppCompatActivity(), TimePickerFragment.TimeDialogListener {

    private var _binding: ActivityRepeatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepeatingAlarmBinding

    private var alarmDao: AlarmDao? = null
    private var _alarmService: AlarmService? = null
    private val alarmService get() = _alarmService as AlarmService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDatabase(this)
        alarmDao = db.alarmDao()

        _alarmService = AlarmService()

        initView()

    }

    private fun initView() {
        binding.apply {
            btnSetTimeRepeating.setOnClickListener {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, TAG_TIME_PICKER)
            }

            btnAddSetRepeatingAlarm.setOnClickListener {
                val time = tvRepeatingTime.text.toString()
                val message = etNoteRepeating.text.toString()

                if (time != "Time"){
                    alarmService.setRepeatingAlarm(
                        applicationContext,
                        AlarmService.TYPE_REPEATING,
                        time,
                        message
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        alarmDao?.addAlarm(
                            Alarm(
                                0,
                                "Repeating Alarm",
                                time,
                                message,
                                AlarmService.TYPE_REPEATING
                            )
                        )
                    }
                }else{
                    Toast.makeText(this@RepeatingAlarmActivity, "Please Set Time Of Alarm", Toast.LENGTH_SHORT).show()
                }

            }

            btnCancelSetRepeatingAlarm.setOnClickListener {  }
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(hourOfDay, minute)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        binding.tvRepeatingTime.text = dateFormat.format(calendar.time)
    }

    //Close the Activity
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}