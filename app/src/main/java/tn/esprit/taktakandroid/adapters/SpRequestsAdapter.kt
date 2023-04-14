package tn.esprit.taktakandroid.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ItemReqCustomerBinding
import tn.esprit.taktakandroid.databinding.ItemReqSpBinding
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

const val TAG="SpRequestsAdapter"
class SpRequestsAdapter (private val fragmentManager: FragmentManager,private var requests: MutableList<Request>) :RecyclerView.Adapter<SpRequestsAdapter.MyRequestsViewHolder>() {

    inner class MyRequestsViewHolder(val mainView: ItemReqSpBinding):RecyclerView.ViewHolder(mainView.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRequestsViewHolder {
        val mainView = ItemReqSpBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MyRequestsViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onBindViewHolder(holder: MyRequestsViewHolder, position: Int) {
        var request=requests[position]
        Glide.with(holder.itemView).load(Constants.IMG_URL +request.customer.pic).into(holder.mainView.ivPic)
        holder.mainView.tvName.text="${request.customer.firstname} ${request.customer.lastname}"
        holder.mainView.tvBidCount.text="Bids: ${request.bids.size}"
        holder.mainView.tvTimeLoc.text="${parseDate(request.date)} in ${request.location}"
        holder.mainView.tvTos.text="${request.tos}"
        holder.mainView.root.setOnClickListener {
               // navigateToSPProfileFragment(sp)

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
    fun setdata(list:MutableList<Request>){
        requests=list
        notifyDataSetChanged()
    }
}