package tn.esprit.taktakandroid.uis.sp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.BidsSPAdapter
import tn.esprit.taktakandroid.databinding.FragmentSpBidsBinding
import tn.esprit.taktakandroid.repositories.BidRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.bid.BidViewModel
import tn.esprit.taktakandroid.uis.common.bid.BidViewModelFactory
import tn.esprit.taktakandroid.utils.Resource

class SPBidsFragment : BaseFragment() {
    val TAG="SPBidsFragment"

    lateinit var viewModel: BidViewModel
    lateinit var bidAdapter: BidsSPAdapter

    lateinit var mainView: FragmentSpBidsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentSpBidsBinding.inflate(layoutInflater)
        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bidRepository=BidRepository()
        viewModel = ViewModelProvider(this, BidViewModelFactory(bidRepository))[BidViewModel::class.java]

        lifecycleScope.launch {
            setupRecyclerView()
            mainView.searchView
                .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        viewModel.filterSent(newText)
                        return false
                    }
                }
                )
            observeTemp()
        }
        swipeLayoutSetup()
        observeViewModel()
    }

    private fun observeTemp() {
        viewModel.sentTempBids.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                mainView.tvInfo.visibility=View.GONE
                mainView.rvBidsSP.visibility=View.VISIBLE
                bidAdapter.setdata(it)
            }
            else{
                if(mainView.spinkitView.visibility!=View.VISIBLE){
                    mainView.tvInfo.visibility=View.VISIBLE
                    mainView.rvBidsSP.visibility=View.GONE
                }

            }
        }
    }

    private fun observeViewModel() {
        viewModel.sentBidsRes.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { bidsResponse ->
                        if (bidsResponse.bids.isNullOrEmpty()) {
                            mainView.tvInfo.visibility = View.VISIBLE
                            mainView.rvBidsSP.visibility = View.GONE
                        } else {
                            bidAdapter.setdata(bidsResponse.bids.toMutableList())
                            mainView.tvInfo.visibility = View.GONE
                            mainView.rvBidsSP.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvBidsSP.visibility = View.GONE
                        mainView.tvInfo.visibility = View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                    mainView.rvBidsSP.visibility = View.GONE
                    mainView.tvInfo.visibility = View.GONE
                }
            }
        })
    }

    private fun setupRecyclerView() {
        bidAdapter = BidsSPAdapter(parentFragmentManager,mutableListOf())
        mainView.rvBidsSP.apply {
            adapter = bidAdapter
            layoutManager = LinearLayoutManager(activity)
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
                viewModel.getSentBidsList()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSentBidsList()
    }
}