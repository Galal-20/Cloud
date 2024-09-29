package com.example.cloud.repository.local.Alarm


import com.example.cloud.database.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun setAlarm(timeInMillis: Long, alarmId: Int)
    suspend fun deleteAlarm(alarm: AlarmEntity)
    fun getAllAlarms(): Flow<List<AlarmEntity>>

}



