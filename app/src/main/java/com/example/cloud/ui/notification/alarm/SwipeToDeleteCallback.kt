package com.example.cloud.ui.notification.alarm

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud.R
import com.example.cloud.database.AlarmEntity
import com.example.cloud.ui.notification.adapter.AlarmAdapter

abstract class SwipeToDeleteCallback(private val adapter: AlarmAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val deleteIcon = R.drawable.delete_forever_white
    private val backgroundPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val cornerRadius = 40f
    private val swipeMargin = 5f

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val alarm = adapter.getItemAt(position)
        onDelete(alarm)
    }

    abstract fun onDelete(alarm: AlarmEntity)

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        if (dX < 0) { // Swiping to the left
            // Draw the rounded rectangle background
            val backgroundRect = RectF(
                itemView.right + dX + swipeMargin, // left
                itemView.top.toFloat(), // top
                itemView.right.toFloat(), // right
                itemView.bottom.toFloat() // bottom
            )
            c.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint)

            // Draw the delete icon
            val deleteIconDrawable = ContextCompat.getDrawable(recyclerView.context, deleteIcon)
            val deleteIconTop =
                itemView.top + ((itemHeight - deleteIconDrawable?.intrinsicHeight!!) ?: 0) / 2
            val deleteIconBottom = deleteIconTop + (deleteIconDrawable.intrinsicHeight ?: 0)
            val deleteIconLeft =
                (itemView.right - deleteIconDrawable.intrinsicWidth) ?: (0 - swipeMargin)
            val deleteIconRight = itemView.right - swipeMargin

            deleteIconDrawable.setBounds(deleteIconLeft.toInt(), deleteIconTop.toInt(), deleteIconRight.toInt(), deleteIconBottom.toInt())
            deleteIconDrawable.draw(c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
