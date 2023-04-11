package tn.esprit.taktakandroid.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.taktakandroid.databinding.ItemReqCustomerBinding
import tn.esprit.taktakandroid.models.entities.Request
import java.text.SimpleDateFormat
import java.util.*

const val TAG="MyRequestsAdapter"
class MyRequestsAdapter (private val fragmentManager: FragmentManager) :RecyclerView.Adapter<MyRequestsAdapter.MyRequestsViewHolder>() {

    inner class MyRequestsViewHolder(val mainView: ItemReqCustomerBinding):RecyclerView.ViewHolder(mainView.root)

    private val differCallback=object :DiffUtil.ItemCallback<Request>(){
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem._id==newItem._id
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem==newItem
        }
    }

    val  differ= AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRequestsViewHolder {
        val mainView = ItemReqCustomerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MyRequestsViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyRequestsViewHolder, position: Int) {
        var request=differ.currentList[position]
        holder.mainView.tvTitle.text=request.tos
        holder.mainView.tvBidCount.text="Bids: ${request.bids.size}"
        holder.mainView.tvDesc.text=request.desc
        holder.mainView.tvTimeLoc.text="${parseDate(request.date)} in ${request.location}"
        holder.mainView.root.setOnClickListener {
               // navigateToSPProfileFragment(sp)
            Log.e(TAG, "clicked", )
        }

    }

  /*  private fun navigateToSPProfileFragment(user: User) {
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
    }*/
    private fun parseDate(date:String):String{
      val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
      inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

      val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
      val formatedDate = inputDateFormat.parse(date)

        return outputDateFormat.format(formatedDate!!)
    }
}