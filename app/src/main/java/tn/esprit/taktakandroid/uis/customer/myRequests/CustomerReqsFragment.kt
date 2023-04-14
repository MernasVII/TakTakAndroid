package tn.esprit.taktakandroid.uis.customer.myRequests

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.CustomerRequestsAdapter
import tn.esprit.taktakandroid.databinding.FragmentCustomerReqsBinding
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.customer.archivedRequests.ArchivedReqsFragment
import tn.esprit.taktakandroid.uis.customer.addRequest.AddRequestFragment
import tn.esprit.taktakandroid.utils.MyRequestTouchHelperCallback
import tn.esprit.taktakandroid.utils.MyRequestTouchHelperListener
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "CustomerReqsFragment"

class CustomerReqsFragment : BaseFragment(), MyRequestTouchHelperListener {
    private lateinit var mainView: FragmentCustomerReqsBinding
    private lateinit var viewModel: MyRequestsViewModel
    lateinit var customerRequestsAdapter: CustomerRequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentCustomerReqsBinding.inflate(layoutInflater)
        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reqRepository = RequestsRepository()
        viewModel = ViewModelProvider(
            this,
            MyRequestsViewModelFactory(reqRepository)
        )[MyRequestsViewModel::class.java]

        mainView.ivArchive.setOnClickListener {
            navigateTo(ArchivedReqsFragment())
        }

        setupRecyclerView()
        swipeLayoutSetup()
        handleGetRequestResult()
        handleDeleteRequestResult()
        mainView.fabAddReq.setOnClickListener {
            navigateTo(AddRequestFragment())
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
        viewModel.tempRequests.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                mainView.tvInfo.visibility = View.GONE
                mainView.rvRequestsCustomer.visibility = View.VISIBLE
                customerRequestsAdapter.setdata(it)

            } else {
                if (mainView.spinkitView.visibility != View.VISIBLE) {
                    mainView.tvInfo.visibility = View.VISIBLE
                    mainView.rvRequestsCustomer.visibility = View.GONE
                }

            }
        }
    }

    private fun handleGetRequestResult() {
        viewModel.myRequestsResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { myRequestsResponse ->

                        if (myRequestsResponse.myRequests.isNullOrEmpty()) {

                            mainView.tvInfo.visibility = View.VISIBLE
                            mainView.rvRequestsCustomer.visibility = View.GONE
                        } else {
                            customerRequestsAdapter.setdata(myRequestsResponse.myRequests.toMutableList())
                            mainView.tvInfo.visibility = View.GONE
                            mainView.rvRequestsCustomer.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false

                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvRequestsCustomer.visibility = View.GONE
                        mainView.tvInfo.visibility = View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                    mainView.rvRequestsCustomer.visibility = View.GONE
                    mainView.tvInfo.visibility = View.GONE
                }
            }
        }
    }

    private fun handleDeleteRequestResult() {
        viewModel.deleteReqResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    //TODO  progressBarVisibility(false,mainView.spinkitView)
                    response.data?.let { deleteResponse ->
                        viewModel.getMyRequests()
                        Toast.makeText(
                            requireContext(),
                            "${deleteResponse.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resource.Error -> {
                    //TODO   progressBarVisibility(false,mainView.spinkitView)

                    response.message?.let { message ->
                        showDialog(message)

                    }
                }
                is Resource.Loading -> {
                    //TODO   progressBarVisibility(true,mainView.spinkitView)

                }
            }
        }
    }


    private fun navigateTo(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()

    }

    private fun setupRecyclerView() {
        customerRequestsAdapter = CustomerRequestsAdapter(parentFragmentManager, mutableListOf())
        mainView.rvRequestsCustomer.apply {
            adapter = customerRequestsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            val itemTouchHelperCallback = MyRequestTouchHelperCallback(
                requireContext(),
                customerRequestsAdapter,
                this@CustomerReqsFragment
            )
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyRequests()
    }

    private fun swipeLayoutSetup() {
        mainView.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.orangeToBG,
                null
            )
        )
        mainView.swipeRefreshLayout.setOnRefreshListener {
            if (mainView.spinkitView.visibility != View.VISIBLE) {
                mainView.searchView.clearFocus()
                mainView.searchView.setQuery("", false)
                viewModel.getMyRequests()
            } else {
                mainView.swipeRefreshLayout.isRefreshing = false

            }

        }
    }

    override fun onRequestSwiped(reqID: String) {
        viewModel.deleteRequest(reqID)

    }


}