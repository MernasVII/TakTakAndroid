package tn.esprit.taktakandroid.uis.common.aptspending

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.AptsListAdapter

class AptItemTouchHelperCallback(
    private val context: Context,
    private val adapter: AptsListAdapter,
    private val listener: AptItemTouchHelperListener
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val apt = adapter.apts[position]
        when (direction) {
            ItemTouchHelper.LEFT -> {
                listener.onAptSwipedLeft(apt._id!!,apt.customer._id!!)
                adapter.notifyItemChanged(position) // add this line to notify adapter of change
            }
            ItemTouchHelper.RIGHT -> {
                listener.onAptSwipedRight(apt._id!!,apt.customer._id!!)
                adapter.notifyItemChanged(position) // add this line to notify adapter of change
            }
            else -> {
                adapter.notifyItemChanged(position) // add this line to notify adapter of change
            }
        }
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
        val iconMargin = (itemView.height - 80) / 2
        val iconSize = 80

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) { // Swiped left
                val shape = GradientDrawable()
                shape.setColor(ContextCompat.getColor(context, R.color.red))
                shape.cornerRadius = 40f // Set the corner radius here

                shape.setBounds(
                    itemView.right + dX.toInt()/2,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                shape.draw(c)

                val icon = ContextCompat.getDrawable(context, R.drawable.ic_decline)
                icon?.setBounds(
                    itemView.right - iconMargin - iconSize,
                    itemView.top + iconMargin,
                    itemView.right - iconMargin,
                    itemView.bottom - iconMargin
                )
                icon?.draw(c)
            } else if (dX > 0) { // Swiped right
                val shape = GradientDrawable()
                shape.setColor(ContextCompat.getColor(context, R.color.green))
                shape.cornerRadius = 40f // Set the corner radius here

                shape.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt()/2,
                    itemView.bottom
                )
                shape.draw(c)

                val icon = ContextCompat.getDrawable(context, R.drawable.ic_accept)
                icon?.setBounds(
                    itemView.left + iconMargin,
                    itemView.top + iconMargin,
                    itemView.left + iconMargin + iconSize,
                    itemView.bottom - iconMargin
                )
                icon?.draw(c)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


}
