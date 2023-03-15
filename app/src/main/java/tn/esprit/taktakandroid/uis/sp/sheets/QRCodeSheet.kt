package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import tn.esprit.taktakandroid.R


class QRCodeSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_q_r_code_sheet, container, false)

        generateQrCode("content",view)
        return view
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

    private fun generateQrCode(qrcodeString: String, view :View) {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap =
            barcodeEncoder.encodeBitmap(
                qrcodeString,
                BarcodeFormat.QR_CODE,
                getScreenMetrics().second,
                getScreenMetrics().first / 2
            )
        val imageViewQrCode: ImageView = view.findViewById(R.id.idIVQrcode) as ImageView
        imageViewQrCode.setImageBitmap(bitmap)
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("Debug", "dissmised onDismiss")
    }


}