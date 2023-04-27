package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import tn.esprit.taktakandroid.databinding.SheetFragmentQrCodeBinding
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.repositories.PaymentRepository
import tn.esprit.taktakandroid.uis.common.payment.PaymentViewModel
import tn.esprit.taktakandroid.uis.common.payment.PaymentViewModelFactory


class QRCodeSheet : BottomSheetDialogFragment() {
    val TAG = "QRCodeSheet"

    private lateinit var mainView: SheetFragmentQrCodeBinding
    private lateinit var viewModel: PaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentQrCodeBinding.inflate(layoutInflater, container, false)
        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val payRepo = PaymentRepository()
        viewModel = ViewModelProvider(
            this,
            PaymentViewModelFactory(payRepo,null)
        )[PaymentViewModel::class.java]
        val payUrl = arguments?.getString("payUrl")
        val customerEmail = arguments?.getString("customerEmail")

        mainView.tvSend.setOnClickListener {
            viewModel.sendLink(customerEmail!!, payUrl!!)
        }

        //get string and pass to function
        generateQrCode(payUrl!!)
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