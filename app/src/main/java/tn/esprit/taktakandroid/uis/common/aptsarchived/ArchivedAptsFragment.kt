package tn.esprit.taktakandroid.uis.common.aptsarchived

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.AptsListAdapter
import tn.esprit.taktakandroid.databinding.FragmentArchivedAptsBinding
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class ArchivedAptsFragment : BaseFragment() {
    val TAG="ArchivedAptsFragment"

    lateinit var viewModel: ArchivedAptsViewModel
    lateinit var aptAdapter: AptsListAdapter

    lateinit var mainView: FragmentArchivedAptsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentArchivedAptsBinding.inflate(layoutInflater)
        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aptRepository = AptRepository()
        viewModel = ViewModelProvider(this, ArchivedAptsViewModelFactory(aptRepository))[ArchivedAptsViewModel::class.java]

        lifecycleScope.launch {
            val cin = AppDataStore.readString(Constants.CIN)
            setupRecyclerView(cin)
            mainView.searchView
                .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        viewModel.filter(newText,cin!!)
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
        viewModel.tempApts.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                mainView.tvInfo.visibility=View.GONE
                mainView.rvApts.visibility=View.VISIBLE
                aptAdapter.setdata(it)

            }
            else{
                if(mainView.spinkitView.visibility!=View.VISIBLE){
                    mainView.tvInfo.visibility=View.VISIBLE
                    mainView.rvApts.visibility=View.GONE
                }

            }
        }
    }

    private fun observeViewModel() {
        viewModel.aptsRes.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { aptsResponse ->
                        aptAdapter.setdata(aptsResponse.appointments.toMutableList())
                        if (aptsResponse.appointments.isNullOrEmpty()) {
                            mainView.tvInfo.visibility = View.VISIBLE
                            mainView.rvApts.visibility = View.GONE
                        } else {
                            mainView.tvInfo.visibility = View.GONE
                            mainView.rvApts.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvApts.visibility = View.GONE
                        mainView.tvInfo.visibility = View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                    mainView.rvApts.visibility = View.GONE
                    mainView.tvInfo.visibility = View.GONE
                }
            }
        })
    }

    private fun setupRecyclerView(cin: String?) {
        aptAdapter = AptsListAdapter(cin, parentFragmentManager, mutableListOf())
        mainView.rvApts.apply {
            adapter = aptAdapter
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
            if(mainView.spinkitView.visibility!=View.VISIBLE) {
                mainView.searchView.clearFocus()
                mainView.searchView.setQuery("", false)
                viewModel.getArchivedAptsList()
            }
            else{
                mainView.swipeRefreshLayout.isRefreshing = false

            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getArchivedAptsList()
    }
}