package tn.esprit.taktakandroid.uis.common.notifs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.NotifsListAdapter
import tn.esprit.taktakandroid.databinding.FragmentNotifsBinding
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.repositories.NotifRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModelFactory
import tn.esprit.taktakandroid.utils.Resource


class NotifsFragment : BaseFragment() {

    lateinit var viewModel: NotifsViewModel
    lateinit var notifAdapter: NotifsListAdapter

    lateinit var mainView: FragmentNotifsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentNotifsBinding.inflate(layoutInflater)
        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[NotifsViewModel::class.java]


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
        viewModel.tempNotifs.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                mainView.tvInfo.visibility=View.GONE
                mainView.rvNotifs.visibility=View.VISIBLE
                notifAdapter.setdata(it)

            }
            else{
                if(mainView.spinkitView.visibility!=View.VISIBLE){
                    mainView.tvInfo.visibility=View.VISIBLE
                    mainView.rvNotifs.visibility=View.GONE
                }

            }
        }
    }

    private fun observeViewModel() {
        viewModel.notifsRes.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.data?.let { notifsResponse ->
                        if (notifsResponse.notifs.isNullOrEmpty()) {
                            mainView.tvInfo.visibility = View.VISIBLE
                            mainView.rvNotifs.visibility = View.GONE
                        } else {
                            notifAdapter.setdata(notifsResponse.notifs.toMutableList())
                            mainView.tvInfo.visibility = View.GONE
                            mainView.rvNotifs.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvNotifs.visibility = View.GONE
                        mainView.tvInfo.visibility = View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                    mainView.rvNotifs.visibility = View.GONE
                    mainView.tvInfo.visibility = View.GONE
                }
            }
        })
    }

    private fun setupRecyclerView() {
        val aptRepository = AptRepository()
        val aptViewModel = ViewModelProvider(this, AptsViewModelFactory(aptRepository))[AptsViewModel::class.java]
        val viewModelScope = CoroutineScope(viewModel.viewModelScope.coroutineContext + Dispatchers.Main)
        notifAdapter = NotifsListAdapter(parentFragmentManager, viewModelScope,viewModel,
            mutableListOf(),aptViewModel,viewLifecycleOwner)
        mainView.rvNotifs.apply {
            adapter = notifAdapter
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
                mainView.swipeRefreshLayout.isRefreshing = false
                mainView.searchView.clearFocus()
                mainView.searchView.setQuery("", false)
                viewModel.getNotifsList()

                viewModel.countMyNotif()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNotifsList()
    }
}
