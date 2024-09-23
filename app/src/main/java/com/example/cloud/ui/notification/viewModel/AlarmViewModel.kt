package com.example.cloud.ui.notification.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloud.database.entity.AlarmEntity
import com.example.cloud.repository.local.Alarm.AlarmRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlarmViewModel(private val alarmRepository: AlarmRepository) : ViewModel() {

    private val _alarms = MutableLiveData<List<AlarmEntity>>()
    val alarms: LiveData<List<AlarmEntity>> get() = _alarms

    fun loadAlarms() {
        viewModelScope.launch {
            alarmRepository.getAllAlarms().collectLatest { alarmList ->
                _alarms.postValue(alarmList)
            }
        }
    }

    fun setAlarm(timeInMillis: Long) {
        val alarmId = System.currentTimeMillis().toInt()
        viewModelScope.launch {
            alarmRepository.setAlarm(timeInMillis, alarmId)
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            alarmRepository.deleteAlarm(alarm)
        }
    }
}
