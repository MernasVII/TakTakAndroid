package tn.esprit.taktakandroid.uis.customer.myRequests

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.MyRequestsAdapter
import tn.esprit.taktakandroid.databinding.FragmentCustomerReqsBinding
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.customer.ArchivedReqsFragment
import tn.esprit.taktakandroid.uis.customer.addRequest.AddRequestFragment
import tn.esprit.taktakandroid.utils.Resource
import java.util.*
import kotlin.time.Duration.Companion.seconds

const val TAG ="CustomerReqsFragment"
class CustomerReqsFragment : BaseFragment() {
    private lateinit var mainView:FragmentCustomerReqsBinding
    private lateinit var viewModel: MyRequestsViewModel
    lateinit var myRequestsAdapter: MyRequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView= FragmentCustomerReqsBinding.inflate(layoutInflater)
        return mainView.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reqRepository = RequestsRepository()
        viewModel = ViewModelProvider(
            this,
            MyRequestsViewModelFactory(reqRepository)
        )[MyRequestsViewModel::class.java]

        mainView.ivArchive.setOnClickListener{
            navigateTo(ArchivedReqsFragment())
        }

        setupRecyclerView()

       viewModel.myRequestsResult.observe(viewLifecycleOwner) { response ->
            when(response){
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.data?.let { myRequestsResponse ->

                        myRequestsAdapter.setdata(myRequestsResponse.myRequests.toMutableList())
                        if (myRequestsResponse.myRequests.isNullOrEmpty()) {
                            mainView.tvInfo.visibility=View.VISIBLE
                            mainView.rvRequestsCustomer.visibility=View.GONE
                        }else{
                            mainView.tvInfo.visibility=View.GONE
                            mainView.rvRequestsCustomer.visibility=View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvRequestsCustomer.visibility=View.GONE
                        mainView.tvInfo.setText(R.string.server_failure)
                        mainView.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        mainView.tvInfo.visibility=View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.spinkitView)
                    mainView.rvRequestsCustomer.visibility=View.GONE
                }
            }
        }
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
        viewModel.tempRequests.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                mainView.tvInfo.visibility=View.GONE
                mainView.rvRequestsCustomer.visibility=View.VISIBLE
                myRequestsAdapter.setdata(it)

            }
            else{
                    if(mainView.spinkitView.visibility!=View.VISIBLE){
                        mainView.tvInfo.visibility=View.VISIBLE
                        mainView.rvRequestsCustomer.visibility=View.GONE
                    }

            }
        }
    }

    private fun setupRecyclerView(){
        myRequestsAdapter= MyRequestsAdapter(parentFragmentManager, mutableListOf())
        mainView.rvRequestsCustomer.apply {
            adapter=myRequestsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }

    private fun navigateTo(fragment:Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()

    }


    override fun onResume() {
        super.onResume()
        viewModel.getMyRequests()
    }


}