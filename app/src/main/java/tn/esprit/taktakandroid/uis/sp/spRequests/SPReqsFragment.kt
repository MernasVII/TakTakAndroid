package tn.esprit.taktakandroid.uis.sp.spRequests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.SpRequestsAdapter
import tn.esprit.taktakandroid.databinding.FragmentSpReqsBinding
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.sp.SPBidsFragment
import tn.esprit.taktakandroid.utils.Resource

class SPReqsFragment : BaseFragment() {

    private val myBidsFragment = SPBidsFragment()
    private lateinit var mainView: FragmentSpReqsBinding
    private lateinit var viewModel: AllRequestsViewModel
    private lateinit var spRequestsAdapter: SpRequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView= FragmentSpReqsBinding.inflate(layoutInflater)
        return mainView.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reqRepository = RequestsRepository()
        viewModel = ViewModelProvider(
            this,
            AllRequestsViewModelFactory(reqRepository)
        )[AllRequestsViewModel::class.java]


        mainView.ivPending.setOnClickListener{
            navigateToMyBids()
        }

        setupRecyclerView()
        swipeLayoutSetup()

        viewModel.allRequestsResult.observe(viewLifecycleOwner) { response ->
            when(response){
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { allReqResponse ->
                        if (allReqResponse.allRequests.isNullOrEmpty()) {
                            mainView.tvInfo.visibility=View.VISIBLE
                            mainView.rvAllRequests.visibility=View.GONE
                        }else{
                            spRequestsAdapter.setdata(allReqResponse.allRequests.toMutableList())
                            mainView.tvInfo.visibility=View.GONE
                            mainView.rvAllRequests.visibility=View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false

                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvAllRequests.visibility=View.GONE
                        mainView.tvInfo.visibility=View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.spinkitView)
                    mainView.rvAllRequests.visibility=View.GONE
                    mainView.tvInfo.visibility=View.GONE
                }
            }
        }
        mainView.searchView
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.filter(newText)
                    return false
                }
            }
            )
        viewModel.tempAllRequests.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                mainView.tvInfo.visibility=View.GONE
                mainView.rvAllRequests.visibility=View.VISIBLE
                spRequestsAdapter.setdata(it)

            }
            else{
                if(mainView.spinkitView.visibility!=View.VISIBLE){
                    mainView.tvInfo.visibility=View.VISIBLE
                    mainView.rvAllRequests.visibility=View.GONE
                }

            }
        }
    }
    private fun setupRecyclerView(){
        spRequestsAdapter= SpRequestsAdapter(parentFragmentManager, mutableListOf())
        mainView.rvAllRequests.apply {
            adapter=spRequestsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.getAllRequests()
    }
    private fun swipeLayoutSetup() {
        mainView.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.orangeToBG,
                null
            )
        )
        mainView.swipeRefreshLayout.setOnRefreshListener {
            if(mainView.spinkitView.visibility!=View.VISIBLE) {
                mainView.searchView.clearFocus()
                mainView.searchView.setQuery("",false)
                viewModel.getAllRequests()
            }
            else{
                mainView.swipeRefreshLayout.isRefreshing = false

            }

        }
    }
    private fun navigateToMyBids() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, myBidsFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}