package com.nanda

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.nanda.alarm.R
import java.util.*

class AlarmService : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val message = intent?.getStringExtra(EXTRA_TYPE)
        val type = intent?.getIntExtra(EXTRA_TYPE, 0)

        var title = when (type){
            TYPE_ONE_TIME -> "One Time Alarm"
            TYPE_REPEATING -> "Repeating Alarm"
            else -> "Something wrong here"
        }

        val requestCode = when(type) {
            TYPE_ONE_TIME -> ID_ONE_TIME
            TYPE_REPEATING -> ID_REPEATING
            else -> -1
        }

        val notificationId = if (type == TYPE_ONE_TIME) ID_ONE_TIME else ID_REPEATING

        if (context != null && message != null) {
            showNotificationAlarm(
                context,
                "Alarm Oii",
                message,
                requestCode
            )
        }
    }

    fun cancelAlarm(context: Context, type: Int){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmService::class.java)
        val requestCode = when (type){
            TYPE_ONE_TIME -> ID_ONE_TIME
            TYPE_REPEATING -> ID_REPEATING
            else -> Log.i("CancelAlarm", "Unknown type of Alarm")
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        if (type == TYPE_ONE_TIME) {
            Toast.makeText(context, "One Time Alarm Canceled", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(context, "Repeating Alarm Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    fun setRepeatingAlarm(context: Context, type: Int, time: String, messege: String){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra(EXTRA_MESSEGE, messege)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray = time.split(":").toTypedArray()
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context, "Success set RepeatingAlarm", Toast.LENGTH_SHORT).show()
    }

    fun setOneTimeAlarm(context: Context, type: Int, date: String, time: String, messege: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra(EXTRA_MESSEGE, messege)
        intent.putExtra(EXTRA_TYPE, type)
//        intent.putExtra("date", date)
//        intent.putExtra("time", time)
        val timeArray = time.split(":").toTypedArray()
        val dateArray = date.split("-").toTypedArray()

        val calendar = Calendar.getInstance()

        //date
        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]))
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1])-1)
        calendar.set(Calendar.DATE, Integer.parseInt(dateArray[0]))

        //time
        calendar.set(Calendar.HOUR, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_ONE_TIME, intent, 0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context, "Success set OneTimeAlarm", Toast.LENGTH_SHORT).show()
    }

    private fun showNotificationAlarm(
        context: Context,
        title: String,
        messege: String,
        notificationId: Int,
    ) {
        val CHANNEL_ID = "smart_alarm"

        val channelName = "SmartAlarm"
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_one_time)
            .setContentTitle(title)
            .setContentText(messege)
            .setSound(ringtone)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        val notif = builder.build()
        notificationManager.notify(notificationId, notif)
    }

    companion object{
        const val EXTRA_MESSEGE = "messege"
        const val EXTRA_TYPE = "type"

        const val ID_ONE_TIME = 101
        const val ID_REPEATING = 102

        const val TYPE_ONE_TIME = 1
        const val TYPE_REPEATING = 0
    }
}