package tn.esprit.taktakandroid.uis.customer.spslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.SPsListAdapter
import tn.esprit.taktakandroid.databinding.FragmentSpsBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.utils.Resource

const val TAG="SPsListFragment"
class SPsFragment : BaseFragment() {

    lateinit var viewModel: SPsViewModel
    lateinit var sPsListAdapter: SPsListAdapter

    lateinit var mainView:FragmentSpsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView=FragmentSpsBinding.inflate(layoutInflater)
        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRepository = UserRepository()
        viewModel = ViewModelProvider(this, SPsViewModelFactory(userRepository)).get(
            SPsViewModel::class.java)

        setupRecyclerView()
        swipeLayoutSetup()
        observeViewModel()

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
        observeTemp()

    }

    private fun observeTemp() {
        viewModel.tempSPs.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                mainView.tvInfo.visibility=View.GONE
                mainView.rvSps.visibility=View.VISIBLE
                sPsListAdapter.setdata(it)

            }
            else{
                if(mainView.spinkitView.visibility!=View.VISIBLE){
                    mainView.tvInfo.visibility=View.VISIBLE
                    mainView.rvSps.visibility=View.GONE
                }

            }
        }
    }
    private fun observeViewModel() {
        viewModel.spsRes.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { spsResponse ->
                        if (spsResponse.users.isNullOrEmpty()) {
                            mainView.tvInfo.visibility=View.VISIBLE
                            mainView.rvSps.visibility=View.GONE
                        }else{
                            sPsListAdapter.setdata(spsResponse.users.toMutableList())
                            mainView.tvInfo.visibility=View.GONE
                            mainView.rvSps.visibility=View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvSps.visibility=View.GONE
                        mainView.tvInfo.visibility=View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.spinkitView)
                    mainView.rvSps.visibility=View.GONE
                    mainView.tvInfo.visibility = View.GONE
                }
            }
        })
    }

    private fun setupRecyclerView(){
        sPsListAdapter= SPsListAdapter(parentFragmentManager,mutableListOf())
        mainView.rvSps.apply {
            adapter=sPsListAdapter
            layoutManager= LinearLayoutManager(activity)
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
            if(mainView.spinkitView.visibility!=View.VISIBLE) {
                mainView.swipeRefreshLayout.isRefreshing = false
                mainView.searchView.clearFocus()
                mainView.searchView.setQuery("", false)
                viewModel.getSPsList()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSPsList()
    }
}