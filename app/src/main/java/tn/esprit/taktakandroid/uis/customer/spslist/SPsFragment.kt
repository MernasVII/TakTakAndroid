package tn.esprit.taktakandroid.uis.customer.spslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.spsResult.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.data?.let { spsResponse ->
                        sPsListAdapter.differ.submitList(spsResponse.users)
                        if (spsResponse.users.isNullOrEmpty()) {
                            mainView.tvInfo.visibility=View.VISIBLE
                            mainView.rvSps.visibility=View.GONE
                        }else{
                            mainView.tvInfo.visibility=View.GONE
                            mainView.rvSps.visibility=View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvSps.visibility=View.GONE
                        mainView.tvInfo.setText(R.string.server_failure)
                        mainView.tvInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        mainView.tvInfo.visibility=View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.spinkitView)
                    mainView.rvSps.visibility=View.GONE
                }
            }
        })
    }

    private fun setupRecyclerView(){
        sPsListAdapter= SPsListAdapter(parentFragmentManager)
        mainView.rvSps.apply {
            adapter=sPsListAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }
}