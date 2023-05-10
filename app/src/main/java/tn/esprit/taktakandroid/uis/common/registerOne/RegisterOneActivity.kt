package tn.esprit.taktakandroid.uis.common.registerOne

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import render.animations.Attention
import render.animations.Render
import tn.esprit.taktakandroid.databinding.ActivityRegisterOneBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.BaseActivity
import tn.esprit.taktakandroid.uis.common.mapView.MapActivity
import tn.esprit.taktakandroid.uis.common.registerTwo.RegisterTwoActivity
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "RegisterOneActivity"

class RegisterOneActivity : BaseActivity() {
    private lateinit var mainView: ActivityRegisterOneBinding
    private lateinit var viewModel: RegisterOneViewModel
    private lateinit var render: Render

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = ActivityRegisterOneBinding.inflate(layoutInflater)
        setContentView(mainView.root)
        render=Render(this)

        val userRepository = UserRepository()
        val viewModelProviderFactory = RegisterOneViewModelProviderFactory(userRepository)

        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[RegisterOneViewModel::class.java]



        setUpEditTexts()
        errorHandling()


        viewModel.signUpResult.observe(this@RegisterOneActivity) { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.progressBar)
                    result.data?.let {
                        lifecycleScope.launch {
                            showSnackbar(it.message, mainView.cl)
                            delay(1000L)
                            finish()
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.progressBar)
                    result.message?.let { msg ->
                        showDialog(msg)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.progressBar)
                }
            }
        }

        mainView.btnRegCust.setOnClickListener {
            viewModel.signUp()
        }
        mainView.etAddress.setOnClickListener {
            PermissionX.init(this).permissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            ).request { allGranted, _, _ ->
                if (allGranted) {
                    if (isLocationEnabled()) {
                        startForLocationResult.launch(
                            Intent(
                                this@RegisterOneActivity,
                                MapActivity::class.java
                            )
                        )
                    }else {
                        Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                        runBlocking {
                            delay(500L)
                        }
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }

                }
            }


        }
        mainView.btnRegSp.setOnClickListener {
            if (viewModel.fieldsValidation(
                    viewModel.firstname.value,
                    viewModel.lastname.value,
                    viewModel.password.value,
                    viewModel.address.value,
                    viewModel.email.value,
                )
            ) Intent(this, RegisterTwoActivity::class.java).apply {
                putExtra("firstname", viewModel.firstname.value)
                putExtra("lastname", viewModel.lastname.value)
                putExtra("password", viewModel.password.value)
                putExtra("address", viewModel.address.value)
                putExtra("email", viewModel.email.value)
                finishOneActivityCallback.launch(this)

            }

        }
    }

    private val startForLocationResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            val location: String

            if (resultCode == Activity.RESULT_OK) {
                location = data!!.getStringExtra("location")!!.replace("null","")

                mainView.etAddress.setText(location)
                viewModel.setAddress(location)

            }
        }

    private fun setUpEditTexts() {
        mainView.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setEmail(s.toString().trim().lowercase())
                viewModel.removeEmailError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mainView.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setPassword(s.toString())
                viewModel.removePwdError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mainView.etFirstname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setFirstname(s.toString())
                viewModel.removeFirstnameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mainView.etLastname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setLastname(s.toString())
                viewModel.removeLastnameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
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
        viewModel.emailError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlEmail.apply {
                    error = viewModel.emailError.value
                    isErrorEnabled = true
                    render.setAnimation(Attention.Shake(mainView.tlEmail))
                    render.start()
                }
            } else {
                mainView.tlEmail.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.passwordError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlPassword.apply {
                    error = viewModel.passwordError.value
                    isErrorEnabled = true
                    render.setAnimation(Attention.Shake(mainView.tlPassword))
                    render.start()
                }
            } else {
                mainView.tlPassword.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.firstnameError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlFirstname.apply {
                    error = viewModel.firstnameError.value
                    isErrorEnabled = true
                    render.setAnimation(Attention.Shake(mainView.tlFirstname))
                    render.start()
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
                    render.setAnimation(Attention.Shake(mainView.tlLastname))
                    render.start()
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
                    render.setAnimation(Attention.Shake(mainView.tlAddress))
                    render.start()
                }
            } else {
                mainView.tlAddress.apply {
                    isErrorEnabled = false
                }
            }
        }

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}