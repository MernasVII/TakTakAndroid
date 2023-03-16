package tn.esprit.taktakandroid.uis.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import tn.esprit.taktakandroid.R

class CustomerReqsFragment : Fragment(R.layout.fragment_customer_reqs) {

    private val archivedReqsFragment = ArchivedReqsFragment()

    private lateinit var ivArchive: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivArchive = view.findViewById(R.id.iv_archive)

        ivArchive.setOnClickListener{
            navigateToArchivedReqs()
        }
    }

    private fun navigateToArchivedReqs() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, archivedReqsFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}