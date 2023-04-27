package tn.esprit.taktakandroid.uis.common.aptspending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.AptsListAdapter
import tn.esprit.taktakandroid.databinding.FragmentPendingAptsBinding
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModelFactory
import tn.esprit.taktakandroid.uis.sp.sheets.AptPriceSheet
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Constants.ACCEPTED_APT_RESULT
import tn.esprit.taktakandroid.utils.Resource

class PendingAptsFragment : BaseFragment(), AptItemTouchHelperListener {
    val TAG = "PendingAptsFragment"

    lateinit var pendingAptsViewModel: PendingAptsViewModel
    lateinit var aptsViewModel: AptsViewModel
    lateinit var aptAdapter: AptsListAdapter

    lateinit var mainView: FragmentPendingAptsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentPendingAptsBinding.inflate(layoutInflater)
        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aptRepository = AptRepository()
        pendingAptsViewModel = ViewModelProvider(
            this,
            PendingAptsViewModelFactory(aptRepository)
        )[PendingAptsViewModel::class.java]
        aptsViewModel =
            ViewModelProvider(this, AptsViewModelFactory(aptRepository))[AptsViewModel::class.java]

        lifecycleScope.launch {
            val cin = AppDataStore.readString(Constants.CIN)
            setupRecyclerView(cin)
            mainView.searchView
                .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        pendingAptsViewModel.filter(newText,cin!!)
                        return false
                    }
                }
                )
            observeTemp()
        }

        swipeLayoutSetup()
        observeViewModel()
        handleAcceptAptResult()
        handleDeclineAptResult()

        parentFragmentManager.setFragmentResultListener(ACCEPTED_APT_RESULT,viewLifecycleOwner) {
                _,_->
            pendingAptsViewModel.getPendingAptsList()
        }
    }

    private fun handleDeclineAptResult() {
        pendingAptsViewModel.declineAptRes.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.data?.let { putResponse ->
                        pendingAptsViewModel.getPendingAptsList()
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

    private fun handleAcceptAptResult() {
        pendingAptsViewModel.acceptAptRes.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.data?.let { putResponse ->
                        pendingAptsViewModel.getPendingAptsList()
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
        pendingAptsViewModel.tempApts.observe(viewLifecycleOwner){
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
        pendingAptsViewModel.aptsRes.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { aptsResponse ->
                        if (aptsResponse.appointments.isNullOrEmpty()) {
                            mainView.tvInfo.visibility = View.VISIBLE
                            mainView.rvApts.visibility = View.GONE
                        } else {
                            aptAdapter.setdata(aptsResponse.appointments.toMutableList())
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
        val viewModelScope =
            CoroutineScope(aptsViewModel.viewModelScope.coroutineContext + Dispatchers.Main)
        aptAdapter = AptsListAdapter(cin, parentFragmentManager, mutableListOf(), viewModelScope, aptsViewModel,viewLifecycleOwner,pendingAptsViewModel)
        mainView.rvApts.apply {
            adapter = aptAdapter
            layoutManager = LinearLayoutManager(requireContext())
            if (!cin.isNullOrEmpty()) {
                val itemTouchHelperCallback = AptItemTouchHelperCallback(
                    requireContext(),
                    aptAdapter,
                    this@PendingAptsFragment
                )
                val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                itemTouchHelper.attachToRecyclerView(this)
            }
        }

    }

    override fun onAptSwipedLeft(aptId: String,customerID:String) {
        pendingAptsViewModel.declineApt(IdBodyRequest(aptId),customerID)
    }

    override fun onAptSwipedRight(aptId: String,customerID:String) {
        val aptPriceSheet = AptPriceSheet()
        val args = Bundle()
        args.putString("aptId", aptId)
        args.putString("customerID", customerID)
        aptPriceSheet.arguments = args
        aptPriceSheet.show(parentFragmentManager, "exampleBottomSheet")

    }

    fun swipeLayoutSetup() {
        mainView.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.orangeToBG,
                null
            )
        )
        mainView.swipeRefreshLayout.setOnRefreshListener {
            mainView.swipeRefreshLayout.isRefreshing = false
            if (mainView.spinkitView.visibility != View.VISIBLE) {
                mainView.searchView.clearFocus()
                mainView.searchView.setQuery("", false)
                pendingAptsViewModel.getPendingAptsList()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        pendingAptsViewModel.getPendingAptsList()
    }
}