package tn.esprit.taktakandroid.uis.common.apts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.AptsListAdapter
import tn.esprit.taktakandroid.databinding.FragmentAptsBinding
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.aptsarchived.ArchivedAptsFragment
import tn.esprit.taktakandroid.uis.common.aptspending.PendingAptsFragment
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class AptsFragment : BaseFragment() {
    val TAG="AptsFragment"

    lateinit var viewModel: AptsViewModel
    lateinit var aptAdapter: AptsListAdapter

    lateinit var mainView: FragmentAptsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentAptsBinding.inflate(layoutInflater)
        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aptRepository = AptRepository()
        viewModel = ViewModelProvider(this, AptsViewModelFactory(aptRepository))[AptsViewModel::class.java]

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

        /*val lytSearch = view.findViewById<TextInputLayout>(R.id.lyt_search)
        val etSearch = view.findViewById<TextInputEditText>(R.id.et_search)

        etSearch.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                lytSearch.hint = null
            } else {
                lytSearch.hint = "Search"
            }
        }*/

        mainView.ivPending.setOnClickListener {
            navigateToPendingApts()
        }
        mainView.ivArchive.setOnClickListener {
            navigateToArchivedApts()
        }
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
        val viewModelScope = CoroutineScope(viewModel.viewModelScope.coroutineContext + Dispatchers.Main)
        aptAdapter = AptsListAdapter(cin, parentFragmentManager,mutableListOf(), viewModelScope,viewModel)
        mainView.rvApts.apply {
            adapter = aptAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun navigateToArchivedApts() {
        val archivedAptsFragment = ArchivedAptsFragment()
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, archivedAptsFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun navigateToPendingApts() {
        val pendingAptsFragment = PendingAptsFragment()
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, pendingAptsFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
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
                viewModel.getAptsList()
            }
            else{
                mainView.swipeRefreshLayout.isRefreshing = false
            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAptsList()
    }
}