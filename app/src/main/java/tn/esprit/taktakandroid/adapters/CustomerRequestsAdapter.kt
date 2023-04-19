package tn.esprit.taktakandroid.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ItemReqCustomerBinding
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.uis.common.RequestDetailsFragment
import tn.esprit.taktakandroid.uis.customer.CustomerBidsFragment
import tn.esprit.taktakandroid.uis.customer.SPProfileFragment
import java.text.SimpleDateFormat
import java.util.*

class CustomerRequestsAdapter (private val fragmentManager: FragmentManager, var requests: MutableList<Request>) :RecyclerView.Adapter<CustomerRequestsAdapter.MyRequestsViewHolder>() {

    inner class MyRequestsViewHolder(val mainView: ItemReqCustomerBinding):RecyclerView.ViewHolder(mainView.root)

   /* private val differCallback=object :DiffUtil.ItemCallback<Request>(){
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem._id==newItem._id
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem==newItem
        }
    }

    val  differ= AsyncListDiffer(this,differCallback)*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRequestsViewHolder {
        val mainView = ItemReqCustomerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MyRequestsViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onBindViewHolder(holder: MyRequestsViewHolder, position: Int) {
        var request=requests[position]
        holder.mainView.tvTitle.text=request.tos
        holder.mainView.tvBidCount.text="Bids: ${request.bids.size}"
        holder.mainView.tvDesc.text=request.desc
        holder.mainView.tvTimeLoc.text="${parseDate(request.date)} in ${request.location}"
        holder.mainView.root.setOnClickListener {
            navigateToBidsListFragment(request)
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

    private fun navigateToReqDetails(request: Request) {
        val customerBidsFragment = CustomerBidsFragment()
        val args = Bundle()
        args.putString("reqId", request._id)
        customerBidsFragment.arguments = args
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, customerBidsFragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun parseDate(date:String):String{
      val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
      inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

      val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
      val formatedDate = inputDateFormat.parse(date)

      return outputDateFormat.format(formatedDate!!)
    }
    fun setdata(list:MutableList<Request>){
        requests=list
        notifyDataSetChanged()
    }
}