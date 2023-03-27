package tn.esprit.taktakandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ReceivedMsgLayoutBinding
import tn.esprit.taktakandroid.databinding.SentMsgLayoutBinding
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

    private class ReceivedMessageViewHolder(val mainView: ReceivedMsgLayoutBinding) :
        RecyclerView.ViewHolder(mainView.root) {


        fun bind(message: ChatMessage) {
            mainView.tvMsgReceived.text = message.content
            mainView.tvTimeReceived.text = message.time

            mainView.tvMsgReceived.setOnClickListener {
                if (mainView.tvTimeReceived.visibility == View.VISIBLE) {
                    mainView.tvTimeReceived.visibility = View.GONE
                    return@setOnClickListener
                }
                mainView.tvTimeReceived.visibility = View.VISIBLE
            }
        }
    }

    private class SentMessageViewHolder(val mainView: SentMsgLayoutBinding) :
        RecyclerView.ViewHolder(mainView.root) {


        fun bind(
            message: ChatMessage
        ) {
            mainView.tvMsgSent.text = message.content
            mainView.tvTimeSent.text = message.time

            mainView.tvMsgSent.setOnClickListener {
                if (mainView.tvTimeSent.visibility == View.VISIBLE) {
                    mainView.tvTimeSent.visibility = View.GONE
                    return@setOnClickListener
                }
                mainView.tvTimeSent.visibility = View.VISIBLE

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MESSAGE_SENT) {
            return SentMessageViewHolder(
                SentMsgLayoutBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            );
        }
        return ReceivedMessageViewHolder(
            ReceivedMsgLayoutBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
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