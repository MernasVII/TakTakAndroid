package tn.esprit.taktakandroid.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ItemAptBinding
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.uis.common.AptDetailsFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.aptspending.PendingAptsViewModel
import tn.esprit.taktakandroid.uis.sp.sheets.PostponeAptSheet
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource
import java.text.SimpleDateFormat
import java.util.*

class AptsListAdapter(
    private val cin: String?,
    private val fragmentManager: FragmentManager,
    var apts: MutableList<Appointment>,
    private val adapterScope: CoroutineScope? = null,
    private val viewModel: AptsViewModel? = null,
    private val owner: LifecycleOwner? = null,
    private val pendingViewModel: PendingAptsViewModel? = null,
    ) : RecyclerView.Adapter<AptsListAdapter.AptViewHolder>() {

    inner class AptViewHolder(mainView: ItemAptBinding): RecyclerView.ViewHolder(mainView.root)

    /*private val differCallback=object : DiffUtil.ItemCallback<Appointment>(){
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem._id==newItem._id
        }

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem==newItem
        }
    }

    val  differ= AsyncListDiffer(this,differCallback)*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AptViewHolder {
        val mainView = ItemAptBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AptViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return apts.size
    }

    override fun onBindViewHolder(holder: AptsListAdapter.AptViewHolder, position: Int) {
        var apt=apts[position]
        var user:User
        holder.itemView.apply {
            user = if(cin.isNullOrEmpty()){
                apt.sp!!
            }else{
                apt.customer!!
            }
            manageViewsVisibility(apt,holder.itemView)
            Glide.with(this).load(Constants.IMG_URL +user.pic).into(holder.itemView.findViewById<CircleImageView>(R.id.iv_pic))
            holder.itemView.findViewById<TextView>(R.id.tv_name).text = user.firstname+" "+user.lastname
            holder.itemView.findViewById<TextView>(R.id.tv_speciality).text = apt.tos
            holder.itemView.findViewById<TextView>(R.id.tv_timeLoc).text = getTime(apt.date)
            holder.itemView.findViewById<Button>(R.id.btn_postpone).setOnClickListener {
                val postponeAptSheet = PostponeAptSheet()
                val args = Bundle()
                args.putString("aptId", apt._id)
                postponeAptSheet.arguments = args
                postponeAptSheet.show(fragmentManager, "exampleBottomSheet")
            }
            holder.itemView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                adapterScope?.launch {
                    val idBodyRequest= apt._id?.let { it1 -> IdBodyRequest(it1) }
                    if (idBodyRequest != null) {
                        viewModel?.cancelApt(idBodyRequest)
                        if(apt.isAccepted){
                            handleCancelRequestResult(holder.itemView.context)
                        }else{
                            handleCancelPspRequestResult(holder.itemView.context)
                        }
                    }
                }
            }

            setOnClickListener {
                navigateToAptDetails(apt)
            }
        }
    }

    private fun handleCancelPspRequestResult(context: Context) {
        viewModel?.putAptRes?.observe(owner!!) { response ->
            when (response) {
                is Resource.Success -> {
                    //TODO  progressBarVisibility(false,mainView.spinkitView)
                    response.data?.let { response ->
                        pendingViewModel?.getPendingAptsList()
                        Toast.makeText(
                            context,
                            "${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resource.Error -> {
                    //TODO   progressBarVisibility(false,mainView.spinkitView)
                    response.message?.let { message ->
                        //showDialog(message)

                    }
                }
                is Resource.Loading -> {
                    //TODO   progressBarVisibility(true,mainView.spinkitView)

                }
            }
        }
    }

    private fun handleCancelRequestResult(context: Context) {
        viewModel?.putAptRes?.observe(owner!!) { response ->
            when (response) {
                is Resource.Success -> {
                    //TODO  progressBarVisibility(false,mainView.spinkitView)
                    response.data?.let { response ->
                        viewModel.getAptsList()
                        Toast.makeText(
                            context,
                            "${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resource.Error -> {
                    //TODO   progressBarVisibility(false,mainView.spinkitView)

                    response.message?.let { message ->
                        //showDialog(message)

                    }
                }
                is Resource.Loading -> {
                    //TODO   progressBarVisibility(true,mainView.spinkitView)

                }
            }
        }
    }

    private fun manageViewsVisibility(apt: Appointment?, itemView: View) {
        if(apt!!.isArchived){
            itemView.findViewById<LinearLayoutCompat>(R.id.ll_btns).visibility=View.GONE
            itemView.findViewById<ImageView>(R.id.iv_aptState).visibility=View.GONE
        }else {
            itemView.findViewById<LinearLayoutCompat>(R.id.ll_btns).visibility = View.VISIBLE
            itemView.findViewById<ImageView>(R.id.iv_aptState).visibility = View.VISIBLE
        }
        if(apt.isAccepted){
            itemView.findViewById<ImageView>(R.id.iv_aptState).setImageResource(R.drawable.ic_accepted)
        }else{
            itemView.findViewById<ImageView>(R.id.iv_aptState).setImageResource(R.drawable.ic_pending)
        }
        if(cin.isNullOrEmpty()){
            itemView.findViewById<Button>(R.id.btn_postpone).visibility=View.GONE
            if(apt.state!=0){
                itemView.findViewById<Button>(R.id.btn_cancel).visibility=View.GONE
            }else{
                itemView.findViewById<Button>(R.id.btn_cancel).visibility=View.VISIBLE
            }
        }else{
            if(apt.isAccepted){
                if(apt.state!=0){
                    itemView.findViewById<Button>(R.id.btn_postpone).visibility=View.GONE
                }else{
                    itemView.findViewById<Button>(R.id.btn_postpone).visibility=View.VISIBLE
                }
            }else{
                itemView.findViewById<Button>(R.id.btn_postpone).visibility=View.GONE
            }
            itemView.findViewById<Button>(R.id.btn_cancel).visibility=View.GONE
        }
    }

    private fun getTime(dateString: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(dateString)

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        dateFormatter.timeZone = TimeZone.getDefault()
        val dateStr = dateFormatter.format(date)

        val timeFormatter = SimpleDateFormat("HH:mm")
        timeFormatter.timeZone = TimeZone.getDefault()
        val timeStr = timeFormatter.format(date)
        return "$dateStr at $timeStr"
    }

    private fun navigateToAptDetails(apt: Appointment) {
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

    fun setdata(list:MutableList<Appointment>){
        apts=list
        notifyDataSetChanged()
    }

}