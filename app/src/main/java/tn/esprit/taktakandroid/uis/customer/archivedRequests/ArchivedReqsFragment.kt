package tn.esprit.taktakandroid.uis.customer.archivedRequests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.CustomerRequestsAdapter
import tn.esprit.taktakandroid.databinding.FragmentArchivedReqsBinding
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.utils.Resource

const val TAG ="ArchivedReqsFragment"

class ArchivedReqsFragment : BaseFragment() {

    private lateinit var mainView: FragmentArchivedReqsBinding
    private lateinit var viewModel: MyArchivedRequestsViewModel
    lateinit var customerRequestsAdapter: CustomerRequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView= FragmentArchivedReqsBinding.inflate(layoutInflater)
        return mainView.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reqRepository = RequestsRepository()
        viewModel = ViewModelProvider(
            this,
            MyArchivedRequestsViewModelFactory(reqRepository,requireActivity().application)
        )[MyArchivedRequestsViewModel::class.java]


        setupRecyclerView()
        swipeLayoutSetup()
        viewModel.myArchivedRequestsResult.observe(viewLifecycleOwner) { response ->
            when(response){
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)

                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { myRequestsResponse ->

                        if (myRequestsResponse.archivedRequests.isNullOrEmpty()) {
                            mainView.tvInfo.visibility=View.VISIBLE
                            mainView.rvArchivedRequests.visibility=View.GONE
                        }else{
                            customerRequestsAdapter.setdata(myRequestsResponse.archivedRequests.toMutableList())

                            mainView.tvInfo.visibility=View.GONE
                            mainView.rvArchivedRequests.visibility=View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.spinkitView)

                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvArchivedRequests.visibility=View.GONE
                       mainView.tvInfo.visibility=View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.spinkitView)
                    mainView.rvArchivedRequests.visibility=View.GONE
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
        viewModel.tempArchivedRequests.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                mainView.tvInfo.visibility=View.GONE
                mainView.rvArchivedRequests.visibility=View.VISIBLE
                customerRequestsAdapter.setdata(it)

            }
            else{
                if(mainView.spinkitView.visibility!=View.VISIBLE){
                    mainView.tvInfo.visibility=View.VISIBLE
                    mainView.rvArchivedRequests.visibility=View.GONE
                }

            }
        }
    }

    private fun setupRecyclerView(){
        customerRequestsAdapter= CustomerRequestsAdapter(parentFragmentManager, mutableListOf())
        mainView.rvArchivedRequests.apply {
            adapter=customerRequestsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyArchivedRequests()
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
                mainView.swipeRefreshLayout.isRefreshing = false
                mainView.searchView.clearFocus()
                mainView.searchView.setQuery("", false)
                viewModel.getMyArchivedRequests()
            }

        }
    }


}