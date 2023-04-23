package tn.esprit.taktakandroid.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ItemReqCustomerBinding
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.uis.customer.bids.CustomerBidsFragment
import java.text.SimpleDateFormat
import java.util.*

class CustomerRequestsAdapter (private val fragmentManager: FragmentManager, var requests: MutableList<Request>) :RecyclerView.Adapter<CustomerRequestsAdapter.MyRequestsViewHolder>() {

    inner class MyRequestsViewHolder(val mainView: ItemReqCustomerBinding):RecyclerView.ViewHolder(mainView.root)

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
        if(!request.isClosed){
            holder.mainView.root.setOnClickListener {
                navigateToBidsListFragment(request)
            }
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