package tn.esprit.taktakandroid.uis.common.userprofile

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.taktakandroid.uis.common.sheets.SettingsSheet
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentUserProfileBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.login.LoginActivity
import tn.esprit.taktakandroid.uis.common.sheets.editprofile.EditProfileSheet
import tn.esprit.taktakandroid.uis.common.sheets.updatepwd.UpdatePasswordSheet
import tn.esprit.taktakandroid.uis.sp.sheets.updatework.UpdateWorkDescriptionSheet
import tn.esprit.taktakandroid.uis.sp.sheets.wallet.WalletSheet
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Constants.AUTH_TOKEN
import tn.esprit.taktakandroid.utils.Resource
import tn.esprit.taktakandroid.utils.SocketService
import java.io.File


const val TAG = "UserProfileFragment"

class UserProfileFragment : BaseFragment() {

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
        viewModel = ViewModelProvider(
            this,
            UserProfileViewModelFactory(userRepository)
        )[UserProfileViewModel::class.java]


        val startForImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == Activity.RESULT_OK) {
                    val uri: Uri = data?.data!!
                    val file = uri.path?.let { File(it) }
                    viewModel.updatePic(file)
                    mainView.ivPic.setImageURI(uri)
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        mainView.ivLogout.setOnClickListener {
            doLogout()
        }

        mainView.flDeleteAcc.setOnClickListener {
            showChoiceDialog("Are you sure you want to delete your account permanently?"){deleteAccountAndLogout()}
        }

        mainView.ivAddPic.setOnClickListener {
            PermissionX.init(this).permissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ).request { allGranted, _, _ ->
                if (allGranted) {
                    ImagePicker.with(this).compress(1024).crop().createIntent {
                        startForImageResult.launch(it)
                    }
                }
            }
        }

        swipeLayoutSetup()
        //get user from request getProfile
        getUser()

        sheetsOnClicks()
        viewModel.updatePicRes.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    response.data?.let { viewModel.getUserProfile() }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    response.message?.let { message ->
                        showDialog(message)

                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                }
            }
        }



        return mainView.root
    }

    private fun deleteAccountAndLogout() {
        lifecycleScope.launch {
            viewModel.deleteUser()
            doLogout()
        }
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
        mainView.ivWallet.setOnClickListener {
            displaySheet(WalletSheet())
        }
    }

    private fun getUser() {
        viewModel.userProfileRes.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    mainView.scrollView.visibility = View.VISIBLE
                    response.data?.let { userProfileResponse ->
                        mainView.tvFullname.visibility = View.VISIBLE
                        mainView.tvAddress.visibility = View.VISIBLE
                        mainView.ivAddPic.isClickable = true
                        mainView.ivLogout.isClickable = true
                        mainView.flEdit.isClickable = true
                        mainView.flWork.isClickable = true
                        mainView.flPwd.isClickable = true
                        mainView.flDeleteAcc.isClickable = true
                        user = userProfileResponse.user
                        //set username and address
                        mainView.tvFullname.text = user.firstname + " " + user.lastname
                        //check and set address
                        if (user.address.isNullOrEmpty()) {
                            mainView.tvAddress.text = "-"
                            mainView.ivError.visibility = View.VISIBLE
                        } else {
                            mainView.tvAddress.text = user.address
                            mainView.ivError.visibility = View.GONE
                        }
                        if (user.cin?.isEmpty() == true) {
                            mainView.flWork.visibility = View.GONE
                            mainView.ivWallet.visibility = View.GONE
                        }
                        Glide.with(this).load(Constants.IMG_URL + user.pic).into(mainView.ivPic)
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    mainView.scrollView.visibility = View.VISIBLE
                    response.message?.let { message ->
                        showDialog(message)
                        mainView.tvFullname.visibility = View.GONE
                        mainView.tvAddress.visibility = View.GONE
                        mainView.ivAddPic.isClickable = false
                        mainView.ivLogout.isClickable = false
                        mainView.flEdit.isClickable = false
                        mainView.flWork.isClickable = false
                        mainView.flPwd.isClickable = false
                        mainView.flDeleteAcc.isClickable = false
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                    mainView.scrollView.visibility = View.GONE
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
            val intent = Intent(requireActivity(), SocketService::class.java)
            requireActivity().stopService(intent)
            googleSignOut()
            val notificationManager=requireActivity().applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            withContext(Dispatchers.Main) {
                Intent(requireActivity(), LoginActivity::class.java).also {
                    startActivity(it)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun googleSignOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestId()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient.signOut()
    }




    fun swipeLayoutSetup() {
        mainView.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.orangeToBG,
                null
            )
        )
        mainView.swipeRefreshLayout.setOnRefreshListener {
            if (mainView.spinkitView.visibility != View.VISIBLE) {
                viewModel.getUserProfile()
            } else {
                mainView.swipeRefreshLayout.isRefreshing = false

            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserProfile()
    }

}