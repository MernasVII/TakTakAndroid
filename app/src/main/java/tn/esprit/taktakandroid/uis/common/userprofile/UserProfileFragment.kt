package tn.esprit.taktakandroid.uis.common.userprofile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.taktakandroid.uis.common.sheets.editprofile.EditProfileSheet
import tn.esprit.miniprojetinterfaces.Sheets.SettingsSheet
import tn.esprit.taktakandroid.uis.common.sheets.updatepwd.UpdatePasswordSheet
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentUserProfileBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.common.login.LoginActivity
import tn.esprit.taktakandroid.uis.sp.sheets.updatework.UpdateWorkDescriptionSheet
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Constants.AUTH_TOKEN
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "UserProfileFragment"

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    lateinit var viewModel: UserProfileViewModel
    lateinit var mainView: FragmentUserProfileBinding
    lateinit var user: User


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentUserProfileBinding.inflate(layoutInflater)
        val userRepository = UserRepository()
        viewModel = ViewModelProvider(this, UserProfileViewModelFactory(userRepository)).get(UserProfileViewModel::class.java)

        //logout
        mainView.ivLogout.setOnClickListener {
            doLogout()
        }

        //get user from request getProfile
        getUser()

        sheetsOnClicks()

        return mainView.root
    }

    private fun sheetsOnClicks() {
        //open settings sheet
        mainView.flSettings.setOnClickListener {
            displaySheet(SettingsSheet())
        }

        //open update profile sheet
        mainView.flEdit.setOnClickListener {
            displaySheet(EditProfileSheet(user))
        }

        //open update work desc sheet
        mainView.flWork.setOnClickListener {
            displaySheet(UpdateWorkDescriptionSheet(user))
        }

        //open update pwd sheet
        mainView.flPwd.setOnClickListener {
            displaySheet(UpdatePasswordSheet(user))
        }
    }

    private fun getUser() {
        viewModel.userProfile.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    // hideProgressBar()
                    response.data?.let { userProfileResponse ->
                        user = userProfileResponse.user
                        //set username and address
                        mainView.tvFullname.text = user.firstname + " " + user.lastname
                        mainView.tvAddress.text = user.address
                        if(user.cin?.isEmpty() == true){
                            mainView.flWork.visibility=View.GONE
                        }
                        Glide.with(this).load(Constants.IMG_URL +user.pic).into(mainView.ivPic)


                    }
                }
                is Resource.Error -> {
                    // hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occurred: $message")
                    }
                }
                is Resource.Loading -> {
                    // showProgressBar()
                }
            }
        })
    }

    private fun displaySheet(bottomSheet: BottomSheetDialogFragment) {
        //bottomSheet.isCancelable = false;
        bottomSheet.show(parentFragmentManager, "exampleBottomSheet")
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