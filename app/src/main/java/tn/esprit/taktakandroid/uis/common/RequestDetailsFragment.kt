package tn.esprit.taktakandroid.uis.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.databinding.FragmentRequestDetailsBinding
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.repositories.BidRepository
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.bid.BidViewModel
import tn.esprit.taktakandroid.uis.common.bid.BidViewModelFactory
import tn.esprit.taktakandroid.uis.sp.sheets.MakeBidSheet
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class RequestDetailsFragment : BaseFragment() {
    private val TAG = "RequestDetailsFragment"

    private lateinit var mainView: FragmentRequestDetailsBinding
    lateinit var viewModel: BidViewModel

    private lateinit var request: Request

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentRequestDetailsBinding.inflate(layoutInflater)
        val bidRepository = BidRepository()
        viewModel =
            ViewModelProvider(this, BidViewModelFactory(bidRepository))[BidViewModel::class.java]
        request = arguments?.getParcelable<Request>("request")!!

        setData(request!!)

        mainView.btnMakeBid.setOnClickListener {
            val makeBidSheet = MakeBidSheet()
            val args = Bundle()
            args.putString("reqId", request._id)
            args.putString("customerID", request.customer._id)
            makeBidSheet.arguments = args
            makeBidSheet.show(parentFragmentManager, "exampleBottomSheet")
        }

        return mainView.root
    }

    private fun setData(request: Request) {
        mainView.tvDatetime.text = getTime(request.date)
        mainView.tvLocation.text = request.location
        mainView.tvTos.text = request.tos
        mainView.tvDesc.text = request.desc
        lifecycleScope.launch {
            val cin = AppDataStore.readString(Constants.CIN)
            if (cin.isNullOrEmpty()) {
                mainView.btnMakeBid.visibility = View.GONE
            } else {
                mainView.btnMakeBid.visibility = View.VISIBLE
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

        val timeFormatter = SimpleDateFormat("HH:mm")
        timeFormatter.timeZone = TimeZone.getDefault()
        val timeStr = timeFormatter.format(date)
        return "$dateStr at $timeStr"
    }

}