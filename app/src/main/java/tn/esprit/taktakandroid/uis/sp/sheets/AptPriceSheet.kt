package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentAptPriceBinding
import tn.esprit.taktakandroid.models.requests.AcceptAptRequest
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.uis.SheetBaseFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModelFactory
import tn.esprit.taktakandroid.uis.common.aptspending.PendingAptsViewModel
import tn.esprit.taktakandroid.uis.common.aptspending.PendingAptsViewModelFactory
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Constants.ACCEPTED_APT_RESULT
import tn.esprit.taktakandroid.utils.Resource
import kotlin.time.Duration.Companion.seconds


class AptPriceSheet : SheetBaseFragment() {
    val TAG="AptPriceSheet"

    private lateinit var mainView: SheetFragmentAptPriceBinding
    lateinit var pendingAptsViewModel: PendingAptsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentAptPriceBinding.inflate(layoutInflater, container, false)
        val aptRepository = AptRepository()
        pendingAptsViewModel = ViewModelProvider(this, PendingAptsViewModelFactory(aptRepository,requireActivity().application))[PendingAptsViewModel::class.java]

        val aptId = arguments?.getString("aptId")
        val customerID = arguments?.getString("customerID")


        mainView.btnProceed.setOnClickListener {
            lifecycleScope.launch {
                pendingAptsViewModel.acceptApt(AcceptAptRequest(aptId!!,mainView.etBalance.text.toString().toFloat()),customerID!!)
            }
        }
        observeViewModel()
        return mainView.root
    }

    private fun observeViewModel() {
        pendingAptsViewModel.acceptAptRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.data?.let {
                        parentFragmentManager.setFragmentResult(ACCEPTED_APT_RESULT, Bundle())
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




}


