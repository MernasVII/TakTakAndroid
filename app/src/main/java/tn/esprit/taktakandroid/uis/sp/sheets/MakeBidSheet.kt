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
import tn.esprit.taktakandroid.databinding.SheetFragmentMakeBidFragmentBinding
import tn.esprit.taktakandroid.models.requests.MakeBidRequest
import tn.esprit.taktakandroid.repositories.BidRepository
import tn.esprit.taktakandroid.uis.common.bid.BidViewModel
import tn.esprit.taktakandroid.uis.common.bid.BidViewModelFactory

class MakeBidSheet : BottomSheetDialogFragment() {
    val TAG="MakeBidSheet"

    private lateinit var mainView: SheetFragmentMakeBidFragmentBinding
    lateinit var viewModel: BidViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentMakeBidFragmentBinding.inflate(layoutInflater, container, false)
        val bidRepository=BidRepository()
        viewModel = ViewModelProvider(this, BidViewModelFactory(bidRepository))[BidViewModel::class.java]
        val reqId = arguments?.getString("reqId")

        mainView.btnSave.setOnClickListener {
            lifecycleScope.launch {
                viewModel.makeBid(MakeBidRequest(mainView.etBid.text.toString().toFloat(),reqId!!))
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