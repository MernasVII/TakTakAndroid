package tn.esprit.taktakandroid.uis.customer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.SPsListAdapter
import tn.esprit.taktakandroid.uis.HomeViewModel
import tn.esprit.taktakandroid.uis.common.HomeActivity
import tn.esprit.taktakandroid.utils.Resource

const val TAG="SPsListFragment"
class SPsFragment : Fragment(R.layout.fragment_sps) {

    lateinit var viewModel: HomeViewModel
    lateinit var sPsListAdapter: SPsListAdapter

    lateinit var rvSPs:RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as HomeActivity).viewModel
        rvSPs=view.findViewById(R.id.rv_sps)
        setupRecyclerView()
        viewModel.sps.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    //hideProgressBar()
                    response.data?.let { spsResponse ->
                        sPsListAdapter.differ.submitList(spsResponse.users)
                    }
                }
                is Resource.Error -> {
                    //hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG,"An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    //showProgressBar()
                }
            }
        })
    }

    /*private fun hideProgressBar(){
        paginationProgressBar.visibility= View.INVISIBLE
    }

    private fun showProgressBar(){
        paginationProgressBar.visibility= View.VISIBLE
    }*/

    private fun setupRecyclerView(){
        sPsListAdapter= SPsListAdapter(parentFragmentManager)
        rvSPs.apply {
            adapter=sPsListAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }
}