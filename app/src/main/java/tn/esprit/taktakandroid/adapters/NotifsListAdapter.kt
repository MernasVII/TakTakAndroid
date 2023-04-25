package tn.esprit.taktakandroid.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ItemNotifBinding
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.models.entities.Bid
import tn.esprit.taktakandroid.models.entities.Notification
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.uis.common.AptDetailsFragment
import tn.esprit.taktakandroid.uis.common.notifs.NotifsViewModel
import java.text.SimpleDateFormat
import java.util.*

class NotifsListAdapter (private val fragmentManager: FragmentManager,
                         private val adapterScope: CoroutineScope? = null,
                         private val viewModel: NotifsViewModel? = null,
                         private var notifs: MutableList<Notification>) : RecyclerView.Adapter<NotifsListAdapter.NotifViewHolder>() {

    inner class NotifViewHolder(mainView: ItemNotifBinding): RecyclerView.ViewHolder(mainView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val mainView = ItemNotifBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return notifs.size
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        var notif=notifs[position]
        holder.itemView.apply {
            if(notif.read){
                holder.itemView.setBackgroundResource(R.drawable.list_item_bg)
            }else{
                holder.itemView.setBackgroundResource(R.drawable.notif_unread_bg)
            }
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
                adapterScope?.launch {
                        viewModel?.markRead(IdBodyRequest(notif._id!!))
                        navigateToBidOrApt(notif.apt,notif.bid)
                }
            }
        }
    }

    private fun navigateToBidOrApt(apt: Appointment?, bid: Bid?) {
        if(apt!=null){
            val bundle = Bundle().apply {
                putParcelable("apt", apt)
            }
            val aptDetailsFragment = AptDetailsFragment()
            aptDetailsFragment.arguments = bundle
            fragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, aptDetailsFragment)
                addToBackStack(null)
                commit()
            }
        }else if(bid!=null){
            //TODO if bid still not accepted+not declined navigate to bids list else find newly created apt by date and sp and navigate to it
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

    fun setdata(list:MutableList<Notification>){
        notifs=list
        notifyDataSetChanged()
    }
}