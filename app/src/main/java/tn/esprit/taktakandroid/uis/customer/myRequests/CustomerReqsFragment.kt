package tn.esprit.taktakandroid.uis.customer.myRequests

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.MyRequestsAdapter
import tn.esprit.taktakandroid.adapters.SPsListAdapter
import tn.esprit.taktakandroid.databinding.FragmentCustomerReqsBinding
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.customer.ArchivedReqsFragment
import tn.esprit.taktakandroid.uis.customer.spslist.SPsViewModel
import tn.esprit.taktakandroid.uis.customer.spslist.SPsViewModelFactory
import tn.esprit.taktakandroid.utils.Resource

const val TAG ="CustomerReqsFragment"
class CustomerReqsFragment : BaseFragment() {
    private lateinit var mainView:FragmentCustomerReqsBinding
    private val archivedReqsFragment = ArchivedReqsFragment()
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
            navigateToArchivedReqs()
        }

        setupRecyclerView()

        viewModel.myRequestsResult.observe(viewLifecycleOwner) { response ->
            when(response){
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.data?.let { myRequestsResponse ->

                        myRequestsAdapter.differ.submitList(myRequestsResponse.myRequests)
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
    }

    private fun setupRecyclerView(){
        myRequestsAdapter= MyRequestsAdapter(parentFragmentManager)
        mainView.rvRequestsCustomer.apply {
            adapter=myRequestsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }

    private fun navigateToArchivedReqs() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, archivedReqsFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}