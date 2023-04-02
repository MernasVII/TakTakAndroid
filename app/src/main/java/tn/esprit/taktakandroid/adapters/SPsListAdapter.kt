package tn.esprit.taktakandroid.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.User
import com.bumptech.glide.Glide
import tn.esprit.taktakandroid.uis.customer.SPProfileFragment
import tn.esprit.taktakandroid.utils.Constants.IMG_URL

class SPsListAdapter (private val fragmentManager: FragmentManager) :RecyclerView.Adapter<SPsListAdapter.SPViewHolder>() {

    inner class SPViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    private val differCallback=object :DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem._id==newItem._id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem==newItem
        }
    }

    val  differ= AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SPViewHolder {
        return SPViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_sp,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SPViewHolder, position: Int) {
        val sp=differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(IMG_URL+sp.pic).into(holder.itemView.findViewById<CircleImageView>(R.id.iv_pic))
            holder.itemView.findViewById<TextView>(R.id.tv_name).text = sp.firstname+" "+sp.lastname
            holder.itemView.findViewById<TextView>(R.id.tv_speciality).text = sp.speciality
            holder.itemView.findViewById<TextView>(R.id.tv_address).text = sp.address
            holder.itemView.findViewById<TextView>(R.id.tv_rate).text = String.format("%.1f", sp.rate)
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
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, spProfileFragment)
            .commit()
    }
}