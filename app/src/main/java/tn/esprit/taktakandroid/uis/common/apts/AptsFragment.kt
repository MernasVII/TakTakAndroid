package tn.esprit.taktakandroid.uis.common.apts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        }

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

    private fun observeViewModel() {
        viewModel.aptsResult.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    response.data?.let { aptsResponse ->
                        aptAdapter.differ.submitList(aptsResponse.appointments)
                        if (aptsResponse.appointments.isNullOrEmpty()) {
                            mainView.tvInfo.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.orangeToYellow
                                )
                            )
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
                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvApts.visibility = View.GONE
                        mainView.tvInfo.setText(R.string.server_failure)
                        mainView.tvInfo.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        mainView.tvInfo.visibility = View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                    mainView.rvApts.visibility = View.GONE
                }
            }
        })
    }

    private fun setupRecyclerView(cin: String?) {
        val viewModelScope = CoroutineScope(viewModel.viewModelScope.coroutineContext + Dispatchers.Main)
        aptAdapter = AptsListAdapter(cin, parentFragmentManager, viewModelScope,viewModel)
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
}