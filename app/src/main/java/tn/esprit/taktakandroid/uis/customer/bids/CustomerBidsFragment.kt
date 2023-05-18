package tn.esprit.taktakandroid.uis.customer.bids

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.BidsCustomerAdapter
import tn.esprit.taktakandroid.databinding.FragmentCustomerBidsBinding
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.repositories.BidRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.RequestDetailsFragment
import tn.esprit.taktakandroid.uis.common.bid.BidViewModel
import tn.esprit.taktakandroid.uis.common.bid.BidViewModelFactory
import tn.esprit.taktakandroid.utils.Resource

class CustomerBidsFragment : BaseFragment(), BidCustomerItemTouchHelperListener {
    val TAG="CustomerBidsFragment"

    lateinit var viewModel: BidViewModel
    lateinit var bidAdapter: BidsCustomerAdapter

    lateinit var mainView: FragmentCustomerBidsBinding

    lateinit var request:Request

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentCustomerBidsBinding.inflate(layoutInflater)

        request = arguments?.getParcelable<Request>("request")!!

        mainView.ivInfo.setOnClickListener {
            navigateToReqDetailsFragment(request)
        }

        return mainView.root
    }

    private fun navigateToReqDetailsFragment(request:Request) {
        val bundle = Bundle().apply {
            putParcelable("request", request)
        }
        val requestDetailsFragment = RequestDetailsFragment()
        requestDetailsFragment.arguments = bundle
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, requestDetailsFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bidRepository= BidRepository()
        viewModel = ViewModelProvider(this, BidViewModelFactory(bidRepository,requireActivity().application))[BidViewModel::class.java]

        lifecycleScope.launch {
            setupRecyclerView()
            mainView.searchView
                .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        viewModel.filterReceived(newText)
                        return false
                    }
                }
                )
            observeTemp()
        }
        swipeLayoutSetup()
        observeViewModel()
        handleAcceptBidResult()
        handleDeclineBidResult()
    }

    private fun handleAcceptBidResult() {
        viewModel.acceptBidRes.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        //Log.d(TAG, "handleAcceptBidResult: ${response.message}")
                        progressBarVisibility(false,mainView.spinkitView)
                        //TODO why response.message is null
                        //Toast.makeText( requireContext(), "${response.message}", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.message?.let { message ->
                        showDialog(message)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.spinkitView)
                }
            }
        }
    }

    private fun handleDeclineBidResult() {
        viewModel.declineBidRes.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.data?.let { putResponse ->
                        viewModel.getReceivedBidsList(IdBodyRequest(request._id))
                        Toast.makeText(
                            requireContext(),
                            "${putResponse.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.spinkitView)

                    response.message?.let { message ->
                        showDialog(message)

                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.spinkitView)
                }
            }
        }
    }

    private fun observeTemp() {
        viewModel.receivedTempBids.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                mainView.tvInfo.visibility= View.GONE
                mainView.rvBidsCustomer.visibility= View.VISIBLE
                bidAdapter.setdata(it)
            }
            else{
                if(mainView.spinkitView.visibility!= View.VISIBLE){
                    mainView.tvInfo.visibility= View.VISIBLE
                    mainView.rvBidsCustomer.visibility= View.GONE
                }

            }
        }
    }

    private fun observeViewModel() {
        viewModel.receivedBidsRes.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { bidsResponse ->
                        if (bidsResponse.receivedBids.isNullOrEmpty()) {
                            mainView.tvInfo.visibility = View.VISIBLE
                            mainView.rvBidsCustomer.visibility = View.GONE
                        } else {
                            bidAdapter.setdata(bidsResponse.receivedBids.toMutableList())
                            mainView.tvInfo.visibility = View.GONE
                            mainView.rvBidsCustomer.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvBidsCustomer.visibility = View.GONE
                        mainView.tvInfo.visibility = View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                    mainView.rvBidsCustomer.visibility = View.GONE
                    mainView.tvInfo.visibility = View.GONE
                }
            }
        })
    }

    private fun setupRecyclerView() {
        bidAdapter = BidsCustomerAdapter(parentFragmentManager,mutableListOf())
        mainView.rvBidsCustomer.apply {
            adapter = bidAdapter
            layoutManager = LinearLayoutManager(activity)
            val itemTouchHelperCallback = BidCustomerItemTouchHelperCallback(
                requireContext(),
                bidAdapter,
                this@CustomerBidsFragment
            )
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    fun swipeLayoutSetup() {
        mainView.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.orangeToBG,
                null
            )
        )
        mainView.swipeRefreshLayout.setOnRefreshListener {
            if(mainView.spinkitView.visibility!= View.VISIBLE) {
                mainView.swipeRefreshLayout.isRefreshing = false
                mainView.searchView.clearFocus()
                mainView.searchView.setQuery("", false)
                viewModel.getReceivedBidsList(IdBodyRequest(request._id))
            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getReceivedBidsList(IdBodyRequest(request._id))
    }

    override fun onBidPendingSwipedLeft(bidId: String,spID:String) {
        showChoiceDialog(requireContext().getString(R.string.are_you_sure)){ viewModel.declineBid(IdBodyRequest(bidId),spID)}
    }

    override fun onBidPendingSwipedRight(bidId: String,spID:String) {
        viewModel.acceptBid(IdBodyRequest(bidId),spID)
    }
}