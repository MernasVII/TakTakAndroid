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
import tn.esprit.taktakandroid.databinding.SheetFragmentAptPriceBinding
import tn.esprit.taktakandroid.models.requests.AcceptAptRequest
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.uis.common.aptspending.PendingAptsViewModel
import tn.esprit.taktakandroid.uis.common.aptspending.PendingAptsViewModelFactory


class AptPriceSheet : BottomSheetDialogFragment() {
    val TAG="AptPriceSheet"

    private lateinit var mainView: SheetFragmentAptPriceBinding
    lateinit var pendingAptsViewModel: PendingAptsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentAptPriceBinding.inflate(layoutInflater, container, false)
        val aptRepository = AptRepository()
        pendingAptsViewModel = ViewModelProvider(this, PendingAptsViewModelFactory(aptRepository))[PendingAptsViewModel::class.java]
        val aptId = arguments?.getString("aptId")

        mainView.btnProceed.setOnClickListener {
            lifecycleScope.launch {
                pendingAptsViewModel.acceptApt(AcceptAptRequest(aptId!!,mainView.etBalance.text.toString().toFloat()))
            }
        }

        return mainView.root
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


