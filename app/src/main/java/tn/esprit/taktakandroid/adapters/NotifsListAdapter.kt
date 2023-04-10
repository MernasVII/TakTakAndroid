package tn.esprit.taktakandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.entities.Notification
import java.text.SimpleDateFormat
import java.util.*

class NotifsListAdapter (private val fragmentManager: FragmentManager) : RecyclerView.Adapter<NotifsListAdapter.NotifViewHolder>() {

    inner class NotifViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val differCallback=object : DiffUtil.ItemCallback<Notification>(){
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem._id==newItem._id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem==newItem
        }
    }

    val  differ= AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        return NotifViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_notif,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        var notif=differ.currentList[position]
        holder.itemView.apply {
            var notifTitle:String
            if(notif.apt!=null){
                notifTitle=holder.itemView.context.getString(R.string.apt)
            }else{
                notifTitle=holder.itemView.context.getString(R.string.bid)
            }
            holder.itemView.findViewById<TextView>(R.id.tv_title).text = notifTitle
            holder.itemView.findViewById<TextView>(R.id.tv_content).text = notif.content
            val time=getTime(notif.createdAt)
            holder.itemView.findViewById<TextView>(R.id.tv_time).text = time
            setOnClickListener {
                //TODO navigateToBidOrApt(notif.apt,notif.bid)
            }
        }
    }

    private fun getTime(dateString: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(dateString)

        val timeFormatter = SimpleDateFormat("hh:mm")
        timeFormatter.timeZone = TimeZone.getDefault()
        return timeFormatter.format(date)
    }
}