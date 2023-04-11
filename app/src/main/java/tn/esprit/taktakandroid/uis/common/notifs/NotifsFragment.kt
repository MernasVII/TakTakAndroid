package tn.esprit.taktakandroid.uis.common.notifs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.NotifsListAdapter
import tn.esprit.taktakandroid.databinding.FragmentNotifsBinding
import tn.esprit.taktakandroid.repositories.NotifRepository
import tn.esprit.taktakandroid.uis.BaseFragment
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
        val notifRepository = NotifRepository()
        viewModel = ViewModelProvider(this, NotifsViewModelFactory(notifRepository))[NotifsViewModel::class.java]

        setupRecyclerView()

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.notifsRes.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    response.data?.let { notifsResponse ->
                        notifAdapter.differ.submitList(notifsResponse.notifs)
                        if (notifsResponse.notifs.isNullOrEmpty()) {
                            mainView.tvInfo.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.orangeToYellow
                                )
                            )
                            mainView.tvInfo.visibility = View.VISIBLE
                            mainView.rvNotifs.visibility = View.GONE
                        } else {
                            mainView.tvInfo.visibility = View.GONE
                            mainView.rvNotifs.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    response.message?.let { message ->
                        showDialog(message)
                        mainView.rvNotifs.visibility = View.GONE
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
                    mainView.rvNotifs.visibility = View.GONE
                }
            }
        })
    }

    private fun setupRecyclerView() {
        notifAdapter = NotifsListAdapter(parentFragmentManager)
        mainView.rvNotifs.apply {
            adapter = notifAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
