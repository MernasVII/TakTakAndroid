package tn.esprit.taktakandroid.uis.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.uis.common.login.LoginActivity
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants.AUTH_TOKEN

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var ivLogout:ImageView
    private lateinit var appDataStore: AppDataStore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDataStore= AppDataStore(requireContext())
        ivLogout = view.findViewById(R.id.iv_logout)

        ivLogout.setOnClickListener{
            doLogout()
        }
    }

    private fun doLogout() {
        lifecycleScope.launch(Dispatchers.IO) {
            appDataStore.deleteString(AUTH_TOKEN)
            withContext(Dispatchers.Main){
                Intent(requireActivity(), LoginActivity::class.java).also {
                    startActivity(it)
                    requireActivity().finish()
                }

            }
        }

    }
}