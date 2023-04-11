package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.databinding.SheetFragmentPostponeAptBinding
import tn.esprit.taktakandroid.models.requests.PostponeAptRequest
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModelFactory


class PostponeAptSheet : BottomSheetDialogFragment() {
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
        viewModel = ViewModelProvider(this, AptsViewModelFactory(aptRepository))[AptsViewModel::class.java]
        val aptId = arguments?.getString("aptId")
        setupPicker()

        mainView.btnPostpone.setOnClickListener {
            lifecycleScope.launch {
                viewModel.postponeApt(PostponeAptRequest(aptId!!,(data[mainView.npMinutes.value - 1]).toInt()))
            }
            dismiss()
        }

        return mainView.root
    }


    fun setupPicker() {

        mainView.npMinutes.apply {
            minValue = 1
            maxValue = data.size
            displayedValues = data
            value = 1
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("Debug", "Dismissed onDismiss")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Debug", "Dismissed onDestroy")
    }


}


