package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentQrCodeBinding
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.repositories.PaymentRepository
import tn.esprit.taktakandroid.uis.SheetBaseFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModelFactory
import tn.esprit.taktakandroid.uis.common.payment.PaymentViewModel
import tn.esprit.taktakandroid.uis.common.payment.PaymentViewModelFactory
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource


class QRCodeSheet : SheetBaseFragment() {
    val TAG = "QRCodeSheet"

    private lateinit var mainView: SheetFragmentQrCodeBinding
    private lateinit var viewModel: PaymentViewModel
    private lateinit var aptViewModel: AptsViewModel
    private lateinit var paymentRef: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentQrCodeBinding.inflate(layoutInflater, container, false)
        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apt = arguments?.getParcelable<Appointment>("apt")!!
        val paymentRepository = PaymentRepository()
        viewModel = ViewModelProvider(
            this,
            PaymentViewModelFactory(paymentRepository,apt)
        )[PaymentViewModel::class.java]

        val aptRepository = AptRepository()
        aptViewModel = ViewModelProvider(this, AptsViewModelFactory(aptRepository))[AptsViewModel::class.java]

        val payUrl = arguments?.getString("payUrl")
        //get string and pass to function
        generateQrCode(payUrl!!)

        mainView.tvSend.setOnClickListener {
            viewModel.sendLink(apt.customer.email!!, payUrl!!)
        }
        observeSendLinkViewModel()

        mainView.btnCheck.setOnClickListener {
            viewModel.paymentStatus(IdBodyRequest(paymentRef))
        }

        observeInitPaymentViewModel()
        observePaymentStatusViewModel(apt)

    }

    private fun observeSendLinkViewModel() {
        viewModel.sendLinkRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.data?.let {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.payment_link_sent),
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun observeInitPaymentViewModel() {
        viewModel.initRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.data?.let {
                        paymentRef=it.paymentRef
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

    private fun observePaymentStatusViewModel(apt: Appointment) {
        viewModel.statusRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        if(!it.isPending){
                            lifecycleScope.launch {
                                aptViewModel?.archiveApt(IdBodyRequest(apt._id!!))
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.payment_succeeded),
                                    Toast.LENGTH_SHORT
                                ).show()
                                parentFragmentManager.setFragmentResult(Constants.QRCODE_PAYMENT_RESULT, Bundle())
                                dismiss()
                            }
                        }
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

    private fun getScreenMetrics(): Pair<Int, Int> {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics

            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout()
            )
            displayMetrics.widthPixels =
                windowMetrics.bounds.width() - insets.left - insets.right
            displayMetrics.heightPixels =
                windowMetrics.bounds.height() - insets.top - insets.bottom
            displayMetrics.density = Resources.getSystem().displayMetrics.density
        } else {
            @Suppress("DEPRECATION")
            val display = requireActivity().windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(displayMetrics)
        }
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return Pair(height, width)
    }

    private fun generateQrCode(qrcodeString: String) {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap =
            barcodeEncoder.encodeBitmap(
                qrcodeString,
                BarcodeFormat.QR_CODE,
                getScreenMetrics().second,
                getScreenMetrics().first / 2
            )
        mainView.idIVQrcode.setImageBitmap(bitmap)
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("Debug", "Dismissed onDismiss")
    }
}