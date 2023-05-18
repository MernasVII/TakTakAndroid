package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.databinding.SheetFragmentMakeBidBinding
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.MakeBidRequest
import tn.esprit.taktakandroid.repositories.BidRepository
import tn.esprit.taktakandroid.uis.SheetBaseFragment
import tn.esprit.taktakandroid.uis.common.bid.BidViewModel
import tn.esprit.taktakandroid.uis.common.bid.BidViewModelFactory
import tn.esprit.taktakandroid.utils.Resource

class MakeBidSheet : SheetBaseFragment() {
    val TAG="MakeBidSheet"

    private lateinit var mainView: SheetFragmentMakeBidBinding
    lateinit var viewModel: BidViewModel

    lateinit var reqId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentMakeBidBinding.inflate(layoutInflater, container, false)
        val bidRepository=BidRepository()
        viewModel = ViewModelProvider(this, BidViewModelFactory(bidRepository,requireActivity().application))[BidViewModel::class.java]
        reqId = arguments?.getString("reqId").toString()
        val customerID=arguments?.getString("customerID").toString()

        mainView.btnSave.setOnClickListener {
            if(mainView.etBid.text.toString().isEmpty() || mainView.etBid.text.toString().toFloat()==0f){
                mainView.tlBid.isErrorEnabled=true
                mainView.tlBid.error="Amount should be greater than 5TND"
            }else{
                mainView.tlBid.isErrorEnabled=false
                lifecycleScope.launch {
                    viewModel.makeBid(MakeBidRequest(mainView.etBid.text.toString().toFloat(),reqId!!),customerID)
                }
            }
        }
        viewModel.getMyBid(IdBodyRequest(reqId))
        observeViewModel()
        observeGetMyBid()
        return mainView.root
    }

    private fun observeGetMyBid() {
        viewModel.getBidPriceRes.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.data?.let { getBidResponse ->
                        if(getBidResponse.price!=null && getBidResponse.price!=0f){
                            mainView.etBid.setText(getBidResponse.price.toString())
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    response.message?.let { message ->
                        showDialog(message)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.spinkitView)
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.makeBidRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.data?.let {
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


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("Debug", "Dismissed onDismiss")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Debug", "Dismissed onDestroy")
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyBid(IdBodyRequest(reqId!!))
    }


}