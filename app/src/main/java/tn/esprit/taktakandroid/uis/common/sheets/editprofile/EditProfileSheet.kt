package tn.esprit.taktakandroid.uis.common.sheets.editprofile

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentEditProfileBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.SheetBaseFragment
import tn.esprit.taktakandroid.uis.common.mapView.MapActivity
import tn.esprit.taktakandroid.uis.common.userprofile.UserProfileFragment
import tn.esprit.taktakandroid.utils.Resource


class EditProfileSheet(private val user: User) : SheetBaseFragment() {
    private val TAG = "EditProfileSheet"

    lateinit var viewModel: EditProfileViewModel
    private lateinit var mainView: SheetFragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentEditProfileBinding.inflate(layoutInflater, container, false)
        val userRepository = UserRepository()
        viewModel = ViewModelProvider(
            this,
            EditProfileViewModelProviderFactory(userRepository)
        )[EditProfileViewModel::class.java]

        setData()
        setUpEditTexts()
        errorHandling()

        observeViewModel()

        mainView.etAddress.setOnClickListener {
            openMap()
        }

        mainView.btnSaveChanges.setOnClickListener {
            lifecycleScope.launch {
                viewModel.updateProfile()
            }
        }

        return mainView.root
    }

    private fun openMap() {
        PermissionX.init(this).permissions(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        ).request { allGranted, _, _ ->
            if (allGranted) {
                if (isLocationEnabled()) {
                    startForLocationResult.launch(
                        Intent(
                            requireContext(),
                            MapActivity::class.java
                        )
                    )
                } else {
                    Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
                    runBlocking {
                        delay(500L)
                    }
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }

            }
        }
    }

    private fun observeViewModel() {
        viewModel.updateProfileRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.data?.let {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.profile_updated),
                            Toast.LENGTH_SHORT
                        ).show()
                        fetchProfile()
                        dismiss()
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.message?.let { msg ->
                        showDialog(msg)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                }
            }
        })
    }

    private fun fetchProfile() {
        // Reload the profile fragment
        val profileFragment = UserProfileFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, profileFragment)
            .commit()
    }

    private fun setData() {
        mainView.etFirstname.setText(user.firstname)
        mainView.etLastname.setText(user.lastname)
        mainView.etEmail.setText(user.email)
        mainView.etAddress.setText(user.address)
    }

    private fun setUpEditTexts() {
        val currentFirstname = mainView.etFirstname.text?.toString()?.trim()
        if (currentFirstname != null) {
            viewModel.setFirstname(currentFirstname)
        }
        mainView.etFirstname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setFirstname(s.toString().trim())
                viewModel.removeFirstnameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val currentLastname = mainView.etLastname.text?.toString()?.trim()
        if (currentLastname != null) {
            viewModel.setLastname(currentLastname)
        }
        mainView.etLastname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setLastname(s.toString())
                viewModel.removeLastnameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val currentAddress = mainView.etAddress.text?.toString()?.trim()
        if (currentAddress != null) {
            viewModel.setAddress(currentAddress)
        }
        mainView.etAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setAddress(s.toString())
                viewModel.removeAddressError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun errorHandling() {
        viewModel.firstnameError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlFirstname.apply {
                    error = viewModel.firstnameError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlFirstname.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.lastnameError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlLastname.apply {
                    error = viewModel.lastnameError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlLastname.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.addressError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlAddress.apply {
                    error = viewModel.addressError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlAddress.apply {
                    isErrorEnabled = false
                }
            }
        }

    }

    private val startForLocationResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            val location: String

            if (resultCode == Activity.RESULT_OK) {
                location = data!!.getStringExtra("location")!!.replace("null", "")

                mainView.etAddress.setText(location)
                viewModel.setAddress(location)

            }
        }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d(TAG, "Dismissed onDismiss")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Dismissed onDestroy")
    }


}