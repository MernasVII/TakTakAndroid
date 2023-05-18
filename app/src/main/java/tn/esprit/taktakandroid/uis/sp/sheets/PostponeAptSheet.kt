package tn.esprit.taktakandroid.uis.sp.sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.databinding.SheetFragmentPostponeAptBinding
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.PostponeAptRequest
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.uis.SheetBaseFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModelFactory
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Constants.POSTPONED_RESULT
import tn.esprit.taktakandroid.utils.Resource
import tn.esprit.taktakandroid.utils.SocketService


class PostponeAptSheet : SheetBaseFragment() {
    val TAG="PostponeAptSheet"


    private lateinit var mainView: SheetFragmentPostponeAptBinding
    lateinit var viewModel: AptsViewModel

    private val data = arrayOf("5", "10", "15", "20", "25", "30")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentPostponeAptBinding.inflate(layoutInflater, container, false)
        val aptRepository = AptRepository()
        viewModel = ViewModelProvider(this, AptsViewModelFactory(aptRepository,requireActivity().application))[AptsViewModel::class.java]
        val aptId = arguments?.getString("aptId")
        val customerID = arguments?.getString("customerID")
        setupPicker()

        mainView.btnPostpone.setOnClickListener {
            lifecycleScope.launch {
                viewModel.postponeApt(PostponeAptRequest(aptId!!,(data[mainView.npMinutes.value - 1]).toInt()),customerID!!)
            }
        }
        observeViewModel(aptId)
        return mainView.root
    }

    private fun observeViewModel(aptId: String?) {
        viewModel.postponeAptRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.data?.let {

                        parentFragmentManager.setFragmentResult(POSTPONED_RESULT, Bundle())
                        dismiss()
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.message?.let { msg ->
                        showDialog(msg)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                }
            }
        })
    }


    private fun setupPicker() {
        mainView.npMinutes.apply {
            minValue = 1
            maxValue = data.size
            displayedValues = data
            value = 1
        }
    }




}


