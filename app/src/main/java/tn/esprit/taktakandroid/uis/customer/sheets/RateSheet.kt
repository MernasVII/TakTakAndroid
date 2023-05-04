package tn.esprit.taktakandroid.uis.customer.sheets

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.databinding.SheetFragmentRateBinding
import tn.esprit.taktakandroid.models.requests.RateBodyRequest
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.uis.SheetBaseFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModelFactory
import tn.esprit.taktakandroid.utils.Resource

class RateSheet : SheetBaseFragment() {
    val TAG="RateSheet"


    private lateinit var mainView: SheetFragmentRateBinding
    lateinit var viewModel: AptsViewModel

    private val scanQrCodeLauncher = registerForActivityResult(ScanQRCode()) { result ->
        when (result) {
            is QRResult.QRSuccess -> {
                val url = result.content.rawValue
                openLinkInBrowser(url)
                dismiss()
            }
            is QRResult.QRUserCanceled -> {
            }
            is QRResult.QRMissingPermission -> {
                Toast.makeText(
                    requireContext(),
                    "Camera permission is required!",
                    Toast.LENGTH_LONG
                ).show()
            }
            is QRResult.QRError -> {
                Toast.makeText(
                    requireContext(),
                    "Error encountered when opening Scanner!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openLinkInBrowser(url: String) {
        val urlIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(urlIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentRateBinding.inflate(layoutInflater, container, false)
        val aptRepository = AptRepository()
        viewModel = ViewModelProvider(this, AptsViewModelFactory(aptRepository))[AptsViewModel::class.java]
        val aptId = arguments?.getString("aptId")

        mainView.btnSave.setOnClickListener {
            lifecycleScope.launch {
                viewModel.rateApt(RateBodyRequest(aptId!!,mainView.ratingbar.rating))
            }
        }
        observeViewModel()
        return mainView.root
    }

    private fun observeViewModel() {
        viewModel.rateAptRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.data?.let {
                        scanQrCodeLauncher.launch(null)
                        //parentFragmentManager.setFragmentResult(Constants.POSTPONED_RESULT, Bundle())
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