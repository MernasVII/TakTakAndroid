package tn.esprit.taktakandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class AptsListAdapter (private val cin: String?, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<AptsListAdapter.AptViewHolder>() {

    inner class AptViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val differCallback=object : DiffUtil.ItemCallback<Appointment>(){
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem._id==newItem._id
        }

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem==newItem
        }
    }

    val  differ= AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AptViewHolder {
        return AptViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_apt,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AptsListAdapter.AptViewHolder, position: Int) {
        var apt=differ.currentList[position]
        var user:User
        holder.itemView.apply {
            if(apt.isArchived){
                holder.itemView.findViewById<LinearLayoutCompat>(R.id.ll_btns).visibility=View.GONE
                holder.itemView.findViewById<ImageView>(R.id.iv_aptState).visibility=View.GONE
            }else {
                holder.itemView.findViewById<LinearLayoutCompat>(R.id.ll_btns).visibility = View.VISIBLE
                holder.itemView.findViewById<ImageView>(R.id.iv_aptState).visibility = View.VISIBLE
            }
            if(apt.isAccepted){
                holder.itemView.findViewById<ImageView>(R.id.iv_aptState).setImageResource(R.drawable.ic_accepted)
            }else{
                holder.itemView.findViewById<ImageView>(R.id.iv_aptState).setImageResource(R.drawable.ic_pending)
            }
            if(cin.isNullOrEmpty()){
                user=apt.sp
                holder.itemView.findViewById<Button>(R.id.btn_postpone).visibility=View.GONE
                holder.itemView.findViewById<Button>(R.id.btn_cancel).visibility=View.VISIBLE
            }else{
                user=apt.customer
                if(apt.isAccepted){
                    holder.itemView.findViewById<Button>(R.id.btn_postpone).visibility=View.VISIBLE
                }else{
                    holder.itemView.findViewById<Button>(R.id.btn_postpone).visibility=View.GONE
                }
                holder.itemView.findViewById<Button>(R.id.btn_postpone).visibility=View.VISIBLE
                holder.itemView.findViewById<Button>(R.id.btn_cancel).visibility=View.GONE
            }
            Glide.with(this).load(Constants.IMG_URL +user.pic).into(holder.itemView.findViewById<CircleImageView>(R.id.iv_pic))
            holder.itemView.findViewById<TextView>(R.id.tv_name).text = user.firstname+" "+user.lastname
            holder.itemView.findViewById<TextView>(R.id.tv_speciality).text = apt.tos
            holder.itemView.findViewById<TextView>(R.id.tv_timeLoc).text = getTime(apt.date)

            holder.itemView.findViewById<Button>(R.id.btn_postpone).setOnClickListener {
                //TODO postpone apt
            }
            holder.itemView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                //TODO cancel apt
            }
            setOnClickListener {
                //TODO navigateToAptDetails(apt)
            }
        }
    }

    private fun getTime(dateString: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(dateString)

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        dateFormatter.timeZone = TimeZone.getDefault()
        val dateStr = dateFormatter.format(date)

        val timeFormatter = SimpleDateFormat("hh:mm")
        timeFormatter.timeZone = TimeZone.getDefault()
        val timeStr = timeFormatter.format(date)
        return "$dateStr at $timeStr"
    }

    /*private fun navigateToSPProfileFragment(user: User) {
        val bundle = Bundle().apply {
            putParcelable("user", user)
        }
        val notifsFragment = NotifsFragment()
        spProfileFragment.arguments = bundle
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, spProfileFragment)
            addToBackStack(null)
            commit()
        }
    }*/
}