package tn.esprit.taktakandroid.uis.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.uis.sp.PendingAptsFragment

class AptsFragment : Fragment(R.layout.fragment_apts) {

    private val pendingAptsFragment = PendingAptsFragment()
    private val archivedAptsFragment = ArchivedAptsFragment()

    private lateinit var ivPending: ImageView
    private lateinit var ivArchive: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivPending = view.findViewById(R.id.iv_pending)
        ivArchive = view.findViewById(R.id.iv_archive)

        /*val lytSearch = view.findViewById<TextInputLayout>(R.id.lyt_search)
        val etSearch = view.findViewById<TextInputEditText>(R.id.et_search)

        etSearch.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                lytSearch.hint = null
            } else {
                lytSearch.hint = "Search"
            }
        }*/

        ivPending.setOnClickListener{
            navigateToPendingApts()
        }
        ivArchive.setOnClickListener{
            navigateToArchivedApts()
        }
    }

    private fun navigateToArchivedApts() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, pendingAptsFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun navigateToPendingApts() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, archivedAptsFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}