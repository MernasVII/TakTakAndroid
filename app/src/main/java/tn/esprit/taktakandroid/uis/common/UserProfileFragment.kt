package tn.esprit.taktakandroid.uis.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import tn.esprit.taktakandroid.R

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var ivLogout:ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivLogout = view.findViewById(R.id.iv_logout)

        ivLogout.setOnClickListener{
            doLogout()
        }
    }

    private fun doLogout() {
        var intent= Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}