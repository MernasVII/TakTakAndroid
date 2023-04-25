package tn.esprit.taktakandroid.uis.customer.addRequest

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.*
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentAddRequestBinding
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.mapView.MapActivity
import tn.esprit.taktakandroid.uis.customer.myRequests.MyRequestsViewModel
import tn.esprit.taktakandroid.uis.customer.myRequests.MyRequestsViewModelFactory
import tn.esprit.taktakandroid.uis.customer.myRequests.TAG
import tn.esprit.taktakandroid.uis.home.HomeActivity
import tn.esprit.taktakandroid.utils.Resource
import java.util.*
import kotlin.time.Duration.Companion.seconds

const val TAG ="AddRequestFragment"
class AddRequestFragment : BaseFragment() {

    private lateinit var mainView: FragmentAddRequestBinding
    private lateinit var viewModel: AddRequestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentAddRequestBinding.inflate(layoutInflater, container, false)

        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reqRepository = RequestsRepository()
        viewModel = ViewModelProvider(
            this,
            AddRequestViewModelFactory(reqRepository)
        )[AddRequestViewModel::class.java]
        errorHandling()
        setupTosSpinner()
        mainView.btnAddReq.setOnClickListener {
            viewModel.setTos(mainView.spService.selectedItem.toString())
            viewModel.addRequest()

        }
        mainView.etDateTime.setOnClickListener {showDatePicker()  }
        mainView.etLocation.setOnClickListener {
            getUserLocation()

        }
        mainView.etDescription.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                viewModel.setDesc(s.toString().trim())
                viewModel.removeDescError()
            }

        })
        viewModel.addReqResult.observe(viewLifecycleOwner){result ->
            when(result){
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.progressBar)
                    result.data?.let {
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                        lifecycleScope.launch {
                            delay(1.seconds)
                            withContext(Dispatchers.Main){
                                requireActivity().supportFragmentManager.popBackStack()
                            }

                        }

                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.progressBar)
                    result.message?.let { msg ->
                        showDialog(msg)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.progressBar)
                }
            }

        }
    }

    private fun getUserLocation() {
        PermissionX.init(this).permissions(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        ).request { allGranted, _, _ ->
            if (allGranted) {
                if (isLocationEnabled()) {
                    startForLocationResult.launch(
                        Intent(
                            requireActivity(),
                            MapActivity::class.java
                        )
                    )
                }else {
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

    private  fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog and set the current date as the default date
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDateDialog,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                mainView.etDateTime.setText("${month+1}/$day/$year")
                viewModel.setDateTime("${month+1}/$day/$year")
                viewModel.removeDateTimeError()
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Set the minimum date to today's date
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        // Show the dialog
        datePickerDialog.show()
    }
    private fun setupTosSpinner() {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item_tos,
            listOf("Installation", "Repair", "Maintenance")
        )
        adapter.setDropDownViewResource(R.layout.spinner_custom_dropdown)
        mainView.spService.adapter = adapter
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private val startForLocationResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            val location: String

            if (resultCode == Activity.RESULT_OK) {
                location = data!!.getStringExtra("location")!!.replace("null","")

                mainView.etLocation.setText(location)
                viewModel.setLocation(location)
                viewModel.removeLocationError()
            }
        }

    private fun errorHandling() {
        viewModel.dateTimeError.observe(viewLifecycleOwner) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlDateTime.apply {
                    error = viewModel.dateTimeError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlDateTime.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.locationError.observe(viewLifecycleOwner) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlLocation.apply {
                    error = viewModel.locationError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlLocation.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.descError.observe(viewLifecycleOwner) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlDescr.apply {
                    error = viewModel.descError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlDescr.apply {
                    isErrorEnabled = false
                }
            }
        }


    }

}