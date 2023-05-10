package tn.esprit.taktakandroid.uis.customer.addRequest

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
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
import com.google.android.material.button.MaterialButton
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.*
import render.animations.Attention
import render.animations.Render
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.seconds

const val TAG ="AddRequestFragment"
class AddRequestFragment : BaseFragment() {

    private lateinit var mainView: FragmentAddRequestBinding
    private lateinit var viewModel: AddRequestViewModel
    private lateinit var tosButtons: List<MaterialButton>
    private var tos=""
    private lateinit var render: Render

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentAddRequestBinding.inflate(layoutInflater, container, false)
        buttonsSetup()

        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        render=Render(requireContext())

        val reqRepository = RequestsRepository()
        viewModel = ViewModelProvider(
            this,
            AddRequestViewModelFactory(reqRepository)
        )[AddRequestViewModel::class.java]
        errorHandling()
        //   setupTosSpinner()
        mainView.btnAddReq.setOnClickListener {
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
        mainView.etDateTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setDateTime(convertDateString(s.toString()))
                viewModel.removeDateTimeError()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        viewModel.addReqResult.observe(viewLifecycleOwner){result ->
            when(result){
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.progressBar)
                    result.data?.let {
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()

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
        // Get current date and time
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Create date picker dialog with a minimum date
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDateDialog,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Create time picker dialog
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, selectedHourOfDay, selectedMinute ->
                        // Handle selected date and time
                        val selectedDateTime = Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, selectedDayOfMonth, selectedHourOfDay, selectedMinute)
                        }.timeInMillis
                        val formattedDateTime = SimpleDateFormat("dd/MM/yyyy 'at' HH:mm", Locale.getDefault())
                            .format(selectedDateTime)
                        mainView.etDateTime.setText(formattedDateTime)
                    },
                    hourOfDay,
                    minute,
                    true
                )
                timePickerDialog.show()
            },
            year,
            month,
            dayOfMonth
        )

        // Disable past dates by setting a minimum date
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }
    /*private fun setupTosSpinner() {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item_tos,
            listOf("Installation", "Repair", "Maintenance")
        )
        adapter.setDropDownViewResource(R.layout.spinner_custom_dropdown)
        mainView.spService.adapter = adapter
    }*/
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
                    render.setAnimation(Attention.Shake(mainView.tlDateTime))
                    render.start()
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
                    render.setAnimation(Attention.Shake(mainView.tlLocation))
                    render.start()
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
                    render.setAnimation(Attention.Shake(mainView.tlDescr))
                    render.start()
                }
            } else {
                mainView.tlDescr.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.tosError.observe(viewLifecycleOwner) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.llTosError.visibility=View.VISIBLE
                render.setAnimation(Attention.Shake(mainView.llTos))
                render.start()
            }else{
                mainView.llTosError.visibility=View.GONE
            }
        }

    }

    private fun buttonsSetup() {
        tosButtons = listOf(
            mainView.btnInstallation, mainView.btnMaintenance, mainView.btnRepair
        )
        tosButtons.forEach {
            it.setOnClickListener { _ ->
                tos=it.text.toString()
                viewModel.setTos(tos)
                mainView.llTosError.visibility=View.GONE
                colorTosBtns(tos)
            }
        }
    }

    private fun colorTosBtns(tos: String) {
        tosButtons.forEach { it ->
            if(it.text.toString().equals(tos)){
                selectBtn(it)
            }else{
                unselectBtn(it)
            }
        }
    }

    private fun selectBtn(btn: MaterialButton) {
        btn.setBackgroundColor(requireActivity().getColor(R.color.BGToLB))
        btn.setTextColor(requireActivity().getColor(R.color.white))
    }

    private fun unselectBtn(btn:MaterialButton) {
        btn.setBackgroundColor(requireActivity().getColor(R.color.white))
        btn.strokeColor = ColorStateList.valueOf(requireActivity().getColor(R.color.BGToLB))
        btn.setTextColor(requireActivity().getColor(R.color.BGToLB))
    }

    fun convertDateString(dateString: String): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy 'at' HH:mm", Locale.getDefault())
        val date = formatter.parse(dateString)
        val timeZone = TimeZone.getTimeZone("UTC")
        val offset = timeZone.getOffset(date.time).toLong()
        val timestamp = date.time - offset
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            .format(Date(timestamp))
    }

}