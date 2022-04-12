package com.nanda.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nanda.data.Alarm

@Database(entities = [Alarm::class], version = 1)
abstract class AlarmDB : RoomDatabase() {

    abstract fun alarmDao() : AlarmDao

    companion object{
        @Volatile
        var instance : AlarmDB? = null

        @JvmStatic
        fun getDatabase(context: Context?): AlarmDB {
            if (instance == null){
                synchronized(AlarmDB::class.java){
                    instance = context?.let {
                        Room.databaseBuilder(
                            it, AlarmDB::class.java, "alarm.db"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return instance as AlarmDB
        }
    }
}