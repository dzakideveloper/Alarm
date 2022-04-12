package com.nanda.alarm

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.RepeatingAlarmActivity
import com.nanda.AlarmService
import com.nanda.adapter.AlarmAdapter
import com.nanda.alarm.databinding.ActivityMainBinding
import com.nanda.data.local.AlarmDB
import com.nanda.data.local.AlarmDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var alarmDao: AlarmDao? = null
    var alarmAdapter: AlarmAdapter? = null

    var alarmService: AlarmService? = null

    override fun onResume() {
        super.onResume()
        alarmDao?.getAlarm()?.observe(this) { data ->
            alarmAdapter?.setData(data)
            Log.i("GetAlarm", "getAlarm : alarm with $data")

        }
//        CoroutineScope(Dispatchers.IO).launch {
//            val alarm = alarmDao?.getAlarm()
//
//            withContext(Dispatchers.Main) {
//                alarmDao?.let {
//                    alarm?.let { it1 -> alarmAdapter?.setData(it1) }
//                }
//                Log.i("GetAlarm", "getAlarm : alarm with $alarm")
//            }
//        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDatabase(applicationContext)
        alarmDao = db.alarmDao()

        alarmAdapter = AlarmAdapter()
        alarmService = AlarmService()

        initview()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvReminderAlarm.apply {

            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
            swipeToDelete(this)
        }
    }

    private fun initview() {
        binding.apply {
            viewSetOneTimeAlarm.setOnClickListener {
                startActivity(Intent(this@MainActivity, OneTimeAlarmActivity::class.java))
            }

            viewSetRepeatingAlarm.setOnClickListener {
                startActivity(Intent(this@MainActivity, RepeatingAlarmActivity::class.java))
            }
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deleteAlarm = alarmAdapter?.listAlarm?.get(viewHolder.adapterPosition)
                CoroutineScope(Dispatchers.IO).launch {
                    deleteAlarm?.let { alarmDao?.deteleAlarm(it) }
                }
                val alarmType = deleteAlarm?.type
                alarmType?.let { alarmService?.cancelAlarm(baseContext, it) }
                alarmAdapter?.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(recyclerView)
    }
}