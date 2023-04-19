package tn.esprit.taktakandroid.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ItemSpBidBinding
import tn.esprit.taktakandroid.models.entities.Bid
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.uis.common.RequestDetailsFragment
import tn.esprit.taktakandroid.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class BidsSPAdapter (
    private val fragmentManager: FragmentManager,
    var bids: MutableList<Bid>
) : RecyclerView.Adapter<BidsSPAdapter.BidSPViewHolder>() {

    inner class BidSPViewHolder(mainView: ItemSpBidBinding): RecyclerView.ViewHolder(mainView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidSPViewHolder {
        val mainView = ItemSpBidBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return BidSPViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return bids.size
    }

    override fun onBindViewHolder(holder: BidsSPAdapter.BidSPViewHolder, position: Int) {
        var bid=bids[position]
        var customer=bid.request.customer
        var req=bid.request
        holder.itemView.apply {
            Glide.with(this).load(Constants.IMG_URL +customer.pic).into(holder.itemView.findViewById<CircleImageView>(
                R.id.iv_pic))
            holder.itemView.findViewById<TextView>(R.id.tv_name).text = customer.firstname+" "+customer.lastname
            holder.itemView.findViewById<TextView>(R.id.tv_tos).text = req.tos
            holder.itemView.findViewById<TextView>(R.id.tv_timeLoc).text = getTime(req.date,req.location)
            val price = if (bid.price % 1 == 0f) bid.price.toInt() else bid.price
            holder.itemView.findViewById<TextView>(R.id.tv_price).text = "Price: "+price+"DT"

            setOnClickListener {
                navigateToReqDetails(req)
            }
        }
    }

    private fun getTime(dateString: String,loc:String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(dateString)

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        dateFormatter.timeZone = TimeZone.getDefault()
        val dateStr = dateFormatter.format(date)
        return "$dateStr in $loc"
    }

    private fun navigateToReqDetails(request: Request) {
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

    fun setdata(list:MutableList<Bid>){
        bids=list
        notifyDataSetChanged()
    }

}