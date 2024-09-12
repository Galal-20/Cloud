package com.example.cloud.ui.favourites

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud.R

class SwipeToDeleteCallBack(private val adapter: FavoriteCitiesAdapter) :
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
        val weatherEntity = adapter.getItem(position)
        adapter.onDeleteClicked(weatherEntity)
    }

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
                itemView.bottom + swipeMargin // bottom
            )
            c.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint)
        }

        // Calculate position of delete icon
        val deleteIconDrawable = ContextCompat.getDrawable(recyclerView.context, deleteIcon)
        deleteIconDrawable?.let {
            val iconMargin = (itemHeight - it.intrinsicHeight) / 2
            val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
            val iconBottom = iconTop + it.intrinsicHeight

            val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
            val iconRight = itemView.right - iconMargin

            // Draw the delete icon
            if (dX < 0) { // Swiping to the left
                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                it.draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}



/*package com.example.cloud.Ui.Favourites

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud.R

class SwipToDeleteCallBack(private val adapter: FavoriteCitiesAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val deleteIcon = R.drawable.delete_forever // Replace with your delete icon resource
    private val background = ColorDrawable(Color.WHITE) // White background color
    private val clearPaint = Paint().apply { xfermode = null }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val weatherEntity = adapter.getItem(position)
        adapter.onDeleteClicked(weatherEntity)
    }

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

        // Draw the white background
        if (dX < 0) { // Swiping to the left
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            background.draw(c)
        }

        // Calculate position of delete icon
        val deleteIconDrawable = ContextCompat.getDrawable(recyclerView.context, deleteIcon)
        deleteIconDrawable?.let {
            val iconMargin = (itemHeight - it.intrinsicHeight) / 2
            val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
            val iconBottom = iconTop + it.intrinsicHeight

            val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
            val iconRight = itemView.right - iconMargin

            // Draw the delete icon
            if (dX < 0) { // Swiping to the left
                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                it.draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}*/


/*package com.example.cloud.ui.favourites

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud.R
import com.example.cloud.Ui.Favourites.FavoriteCitiesAdapter

class SwipeToDeleteCallback(
    private val adapter: FavoriteCitiesAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteIcon = R.drawable.ic_delete_forever // Change to your delete icon resource
    private val background = ColorDrawable(Color.RED) // Red background color
    private val clearPaint = Paint().apply { xfermode = null }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false // We don't support moving items up and down
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val weatherEntity = adapter.getItem(position) // Get item to delete
        adapter.onDeleteClicked(weatherEntity) // Perform delete action
    }

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

        // Draw the red delete background
        if (dX > 0) { // Swiping to the right
            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
        } else if (dX < 0) { // Swiping to the left
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        } else { // No swipe
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)

        // Calculate position of delete icon
        val iconMargin = (itemHeight - deleteIcon.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemHeight - deleteIcon.intrinsicHeight) / 2
        val iconBottom = iconTop + deleteIcon.intrinsicHeight

        if (dX > 0) { // Swiping to the right
            val iconLeft = itemView.left + iconMargin
            val iconRight = iconLeft + deleteIcon.intrinsicWidth
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        } else if (dX < 0) { // Swiping to the left
            val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        }

        deleteIcon.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
*/