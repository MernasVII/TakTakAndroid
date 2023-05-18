package tn.esprit.taktakandroid.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ItemNotifBinding
import tn.esprit.taktakandroid.models.entities.*
import tn.esprit.taktakandroid.models.requests.FindAptRequest
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.uis.common.AptDetailsFragment
import tn.esprit.taktakandroid.uis.common.RequestDetailsFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.notifs.NotifsViewModel
import tn.esprit.taktakandroid.uis.customer.bids.CustomerBidsFragment
import tn.esprit.taktakandroid.utils.Resource
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class NotifsListAdapter(
    private val cin: String?,
    private val fragmentManager: FragmentManager,
    private val adapterScope: CoroutineScope? = null,
    private val viewModel: NotifsViewModel,
    private var notifs: MutableList<Notification>,
    private val aptViewModel: AptsViewModel,
    private val owner: LifecycleOwner? = null
) : RecyclerView.Adapter<NotifsListAdapter.NotifViewHolder>() {

    inner class NotifViewHolder(mainView: ItemNotifBinding) : RecyclerView.ViewHolder(mainView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val mainView = ItemNotifBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return notifs.size
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        var notif = notifs[position]
        holder.itemView.apply {
            if (notif.read) {
                holder.itemView.setBackgroundResource(R.drawable.list_item_bg)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.notif_unread_bg)
            }
            var notifTitle: String
            if (notif.apt != null) {
                notifTitle = holder.itemView.context.getString(R.string.apt)
            } else {
                notifTitle = holder.itemView.context.getString(R.string.bid)
            }
            holder.itemView.findViewById<TextView>(R.id.tv_title).text = notifTitle
            holder.itemView.findViewById<TextView>(R.id.tv_content).text = notif.content
            val time = getTime(notif.createdAt)
            holder.itemView.findViewById<TextView>(R.id.tv_time).text = time
            setOnClickListener {
                adapterScope?.launch {
                    if(!notif.read){
                        viewModel?.markRead(IdBodyRequest(notif._id!!))
                    }
                    navigateToBidOrApt(notif,holder.itemView)
                    delay(200.milliseconds)
                    viewModel?.countMyNotif()
                }
                observeFindApt(holder.itemView.context)
            }
        }
    }

    private fun navigateToBidOrApt(notif: Notification, item: View) {
        if (notif.apt != null) {
            val bundle = Bundle().apply {
                putParcelable("apt", notif.apt)
            }
            val aptDetailsFragment = AptDetailsFragment()
            aptDetailsFragment.arguments = bundle
            fragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, aptDetailsFragment)
                addToBackStack(null)
                commit()
            }
        } else if (notif.bid != null) {
            if(notif.bid.request.isClosed){
                if(!notif.read){
                    item.setBackgroundResource(R.drawable.list_item_bg)
                }
                Toast.makeText(item.context, item.context.getString(R.string.request_closed), Toast.LENGTH_SHORT).show()
            }else{
                if (!notif.bid.isAccepted) {
                    if (cin.isNullOrEmpty()) {
                        navigateToBidsListFragment(notif.bid.request)
                    }else{
                        navigateToReqDetailsFragment(notif.bid.request)
                    }
                } else {
                    aptViewModel.findApt(FindAptRequest(notif.bid.request.date, notif.bid.sp._id!!))
                }
            }
        }else{
            if(!notif.read){
                item.setBackgroundResource(R.drawable.list_item_bg)
            }
            Toast.makeText(item.context, item.context.getString(R.string.request_deleted), Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToBidsListFragment(request: Request) {
        val bundle = Bundle().apply {
            putParcelable("request", request)
        }
        val customerBidsFragment = CustomerBidsFragment()
        customerBidsFragment.arguments = bundle
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, customerBidsFragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun observeFindApt(context: Context) {
        aptViewModel.findAptRes.observe(owner!!) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { response ->
                        navigateToAptDetailsFragment(response.apt)
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Toast.makeText(context, context.getString(R.string.apt_not_found), Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                }
            }
        }
    }

    private fun navigateToAptDetailsFragment(apt: Appointment) {
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
    }


    private fun navigateToReqDetailsFragment(request: Request) {
        val bundle = Bundle().apply {
            putParcelable("request", request)
        }
        val requestDetailsFragment = RequestDetailsFragment()
        requestDetailsFragment.arguments = bundle
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, requestDetailsFragment)
            addToBackStack(null)
            commit()
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

    fun setdata(list: MutableList<Notification>) {
        notifs = list
        notifyDataSetChanged()
    }
}