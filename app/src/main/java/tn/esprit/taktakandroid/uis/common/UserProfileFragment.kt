package tn.esprit.taktakandroid.uis.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.uis.common.login.LoginActivity
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants.AUTH_TOKEN

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
        lifecycleScope.launch(Dispatchers.IO) {
            AppDataStore.deleteString(AUTH_TOKEN)
            googleSignOut()
            withContext(Dispatchers.Main){
                Intent(requireActivity(), LoginActivity::class.java).also {
                    startActivity(it)
                    requireActivity().finish()
                }
            }
        }

    }
    private fun googleSignOut(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestId()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient.signOut()
    }
}