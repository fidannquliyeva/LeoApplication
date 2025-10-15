package com.example.leoapplication.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.leoapplication.R

class SwipeDeleteCallback(
    private val onSwipeDelete: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT
) {

    private val background = ColorDrawable(Color.RED)
    private var deleteIcon: Drawable? = null

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        onSwipeDelete(position)
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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20

        if (deleteIcon == null) {
            deleteIcon = ContextCompat.getDrawable(
                recyclerView.context,
                R.drawable.ic_delete_white
            )
        }

        val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - deleteIcon!!.intrinsicHeight) / 2
        val iconBottom = iconTop + deleteIcon!!.intrinsicHeight

        when {
            dX < 0 -> { // Sola swipe
                val iconLeft = itemView.right - iconMargin - deleteIcon!!.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                deleteIcon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
            }
            else -> {
                background.setBounds(0, 0, 0, 0)
                deleteIcon?.setBounds(0, 0, 0, 0)
            }
        }

        background.draw(c)
        deleteIcon?.draw(c)
    }
}