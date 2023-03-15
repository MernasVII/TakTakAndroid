package tn.esprit.taktakandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.ChatMessage

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val MESSAGE_SENT = 1


    private val diffCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
    val diff = AsyncListDiffer(this, diffCallback)

    private class ReceivedMessageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTV: TextView
        var dateTV: TextView

        init {
            messageTV = itemView.findViewById<TextView>(R.id.tvMsgReceived)
            dateTV = itemView.findViewById<TextView>(R.id.tvTimeReceived)
        }

        fun bind(message: ChatMessage) {
            messageTV.text = message.content
            dateTV.text = message.time

            messageTV.setOnClickListener {
                if (dateTV.visibility == View.VISIBLE) {
                    dateTV.visibility = View.GONE
                    return@setOnClickListener
                }
                dateTV.visibility = View.VISIBLE
            }
        }
    }

    private class SentMessageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTV: TextView
        var dateTV: TextView

        init {
            messageTV = itemView.findViewById<TextView>(R.id.tvMsgSent)
            dateTV = itemView.findViewById<TextView>(R.id.tvTimeSent)
        }

        fun bind(
            message: ChatMessage
        ) {
            messageTV.text = message.content
            dateTV.text = message.time

            messageTV.setOnClickListener {
                if (dateTV.visibility == View.VISIBLE) {
                    dateTV.visibility = View.GONE
                    return@setOnClickListener
                }
                dateTV.visibility = View.VISIBLE

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MESSAGE_SENT) {
            return SentMessageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.sent_msg_layout, parent, false)
            );
        }
        return ReceivedMessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.received_msg_layout, parent, false)
        );

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = diff.currentList[position]
        if (message.sent) {
            (holder as SentMessageViewHolder).bind(message)
        } else {
            (holder as ReceivedMessageViewHolder).bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (diff.currentList[position].sent) {
            return 1
        }
        return 0
    }


    override fun getItemCount(): Int {
        return diff.currentList.size
    }
}