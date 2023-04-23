package tn.esprit.taktakandroid.uis.sp.bids

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.BidsSPAdapter

class BidSPItemTouchHelperCallback(
    private val context: Context,
    private val adapter: BidsSPAdapter,
    private val listener: BidSPItemTouchHelperListener
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
        val bid = adapter.bids[position]
        when (direction) {
            ItemTouchHelper.LEFT,ItemTouchHelper.RIGHT  -> {
                listener.onBidSPSwiped(bid._id!!)
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
                shape.setColor(ContextCompat.getColor(context, R.color.red))
                shape.cornerRadius = 40f // Set the corner radius here

                shape.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt()/2,
                    itemView.bottom
                )
                shape.draw(c)

                val icon = ContextCompat.getDrawable(context, R.drawable.ic_decline)
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
