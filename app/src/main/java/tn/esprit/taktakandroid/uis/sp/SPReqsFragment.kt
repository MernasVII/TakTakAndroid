package tn.esprit.taktakandroid.uis.sp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import tn.esprit.taktakandroid.R

class SPReqsFragment : Fragment(R.layout.fragment_sp_reqs) {

    private val pendingBidsFragment = PendingBidsFragment()

    private lateinit var ivPending: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivPending = view.findViewById(R.id.iv_pending)

        ivPending.setOnClickListener{
            navigateToPendingBids()
        }
    }

    private fun navigateToPendingBids() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, pendingBidsFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}