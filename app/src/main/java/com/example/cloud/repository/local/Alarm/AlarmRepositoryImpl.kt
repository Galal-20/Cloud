package com.example.cloud.repository.local.Alarm


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.cloud.database.dao.AlarmDao
import com.example.cloud.database.entity.AlarmEntity
import com.example.cloud.ui.notification.alarm.AlarmReceiver
import kotlinx.coroutines.flow.Flow

class AlarmRepositoryImpl(
    private val context: Context,
    private val alarmDao: AlarmDao,
    private val alarmManager: AlarmManager
) : AlarmRepository {

    @SuppressLint("ScheduleExactAlarm")
    override suspend fun setAlarm(timeInMillis: Long, alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

        val alarmEntity = AlarmEntity(id = alarmId, timeInMillis = timeInMillis)
        alarmDao.insertAlarm(alarmEntity)
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)

        alarmDao.deleteAlarm(alarm)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return alarmDao.getAllAlarms()
    }




}




