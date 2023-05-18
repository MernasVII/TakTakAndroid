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
import tn.esprit.taktakandroid.databinding.ItemCustomerBidBinding
import tn.esprit.taktakandroid.models.entities.Bid
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.uis.customer.SPProfileFragment
import tn.esprit.taktakandroid.utils.Constants

class BidsCustomerAdapter (
    private val fragmentManager: FragmentManager,
    var bids: MutableList<Bid>
) : RecyclerView.Adapter<BidsCustomerAdapter.BidCustomerViewHolder>() {

    inner class BidCustomerViewHolder(mainView: ItemCustomerBidBinding): RecyclerView.ViewHolder(mainView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidCustomerViewHolder {
        val mainView = ItemCustomerBidBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return BidCustomerViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return bids.size
    }

    override fun onBindViewHolder(holder: BidsCustomerAdapter.BidCustomerViewHolder, position: Int) {
        var bid=bids[position]
        var sp=bid.sp
        holder.itemView.apply {

            var userImage=Constants.IMG_URL + sp.pic
            if(sp.pic!!.lowercase().contains("http")) userImage = sp.pic!!
            Glide.with(this).load(userImage).into(holder.itemView.findViewById<CircleImageView>(
                R.id.iv_pic))
            holder.itemView.findViewById<TextView>(R.id.tv_name).text = sp.firstname+" "+sp.lastname
            val price = if (bid.price % 1 == 0f) bid.price.toInt() else bid.price
            holder.itemView.findViewById<TextView>(R.id.tv_price).text = holder.itemView.context.getString(R.string.price)+": "+price+holder.itemView.context.getString(R.string.dt)

            setOnClickListener {
                navigateToSPProfileFragment(sp)
            }
        }
    }

    private fun navigateToSPProfileFragment(user: User) {
        val bundle = Bundle().apply {
            putParcelable("user", user)
        }
        val spProfileFragment = SPProfileFragment()
        spProfileFragment.arguments = bundle
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, spProfileFragment)
            addToBackStack(null)
            commit()
        }
    }

    fun setdata(list:MutableList<Bid>){
        bids=list
        notifyDataSetChanged()
    }

}