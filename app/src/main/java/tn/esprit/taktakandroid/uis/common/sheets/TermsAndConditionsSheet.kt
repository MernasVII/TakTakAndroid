package tn.esprit.taktakandroid.uis.common.sheets

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentSettingsSheetBinding
import tn.esprit.taktakandroid.databinding.FragmentTermsAndConditionsSheetBinding
import tn.esprit.taktakandroid.utils.Constants.TERMS_URL

class TermsAndConditionsSheet : BottomSheetDialogFragment() {
    private lateinit var mainView: FragmentTermsAndConditionsSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainView = FragmentTermsAndConditionsSheetBinding.inflate(layoutInflater, container, false)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_HALF_EXPANDED
        return mainView.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainView.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(TERMS_URL)
            settings.useWideViewPort=true
            setInitialScale(1)
            setOnTouchListener { v, event ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                v.onTouchEvent(event)
                true
            }
        }
    }


}