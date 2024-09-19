package com.example.cloud.ui.notification.alarm

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud.R
import com.example.cloud.database.AlarmEntity
import com.example.cloud.database.AppDatabase
import com.example.cloud.ui.notification.adapter.AlarmAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class AlertBottomSheetDialog : BottomSheetDialogFragment() {

    private var selectedTimeInMillis: Long = 0
    private lateinit var alarmAdapter: AlarmAdapter

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.alert_bottom_sheet_dialog, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.time_date_recycler_view)
        alarmAdapter = AlarmAdapter { alarm ->
            deleteAlarm(alarm)
        }
        recyclerView.adapter = alarmAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadAlarmsFromDatabase()

        val openPickerButton: FloatingActionButton = view.findViewById(R.id.open_picker_date)
        openPickerButton.setOnClickListener {
            showDatePicker()
        }

        setupSwipeToDelete(recyclerView)

        return view
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView) {
        val swipeHandler = object : SwipeToDeleteCallback(alarmAdapter) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val alarm = alarmAdapter.getItemAt(position)
                deleteAlarm(alarm)
            }

            override fun onDelete(alarm: AlarmEntity) {
                TODO("Not yet implemented")
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showTimePicker(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(calendar: Calendar) {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                selectedTimeInMillis = calendar.timeInMillis
                checkOverlayPermission()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun setAlarm(timeInMillis: Long) {
        val alarmId = System.currentTimeMillis().toInt()
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

        saveAlarmToDatabase(timeInMillis, alarmId)

        Toast.makeText(requireContext(), "Alarm set for the selected time!", Toast.LENGTH_SHORT).show()
    }


    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(requireContext())) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + requireContext().packageName)
                )
                startActivityForResult(intent, 1000)
            } else {
                checkExactAlarmPermission()
            }
        } else {
            checkExactAlarmPermission()
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivityForResult(intent, 2000)
            } else {
                setAlarm(selectedTimeInMillis)
            }
        } else {
            setAlarm(selectedTimeInMillis)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1000 -> {
                if (Settings.canDrawOverlays(requireContext())) {
                    checkExactAlarmPermission()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Overlay permission is required to display the alarm screen.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            2000 -> {
                val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (alarmManager.canScheduleExactAlarms()) {
                    setAlarm(selectedTimeInMillis)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Exact alarm permission is required to set alarms.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveAlarmToDatabase(timeInMillis: Long, alarmId: Int) {
        val alarmEntity = AlarmEntity(id = alarmId, timeInMillis = timeInMillis)
        val db = AppDatabase.getDatabase(requireContext())
        val alarmDao = db.alarmDao()

        lifecycleScope.launch {
            alarmDao.insertAlarm(alarmEntity)
            loadAlarmsFromDatabase()
        }
    }

    private fun deleteAlarm(alarm: AlarmEntity) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)

        pendingIntent.cancel()

        val db = AppDatabase.getDatabase(requireContext())
        val alarmDao = db.alarmDao()

        lifecycleScope.launch {
            alarmDao.deleteAlarm(alarm)
            loadAlarmsFromDatabase()
        }
    }


    private fun loadAlarmsFromDatabase() {
        val db = AppDatabase.getDatabase(requireContext())
        val alarmDao = db.alarmDao()

        lifecycleScope.launch {
            alarmDao.getAllAlarms().collectLatest { alarms ->
                alarmAdapter.submitList(alarms)
            }
        }
    }
}


