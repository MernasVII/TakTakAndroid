package tn.esprit.taktakandroid.uis.customer.bookapt

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentBookAptBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.mapView.MapActivity
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource
import java.text.SimpleDateFormat
import java.util.*

class BookAptFragment : BaseFragment() {
    private val TAG = "BookAptFragment"

    private lateinit var mainView: FragmentBookAptBinding
    lateinit var viewModel: BookAptViewModel

    private lateinit var tosButtons: List<MaterialButton>
    private lateinit var sp: User
    private var tos=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentBookAptBinding.inflate(layoutInflater)
        sp = arguments?.getParcelable("sp")!!
        val aptRepository = AptRepository()
        viewModel = ViewModelProvider(this, BookAptViewModelFactory(aptRepository,sp))[BookAptViewModel::class.java]

        buttonsSetup()
        editTextsSetup()
        inputsErrorHandling()

        setData(sp!!)

        mainView.etDatetime.setOnClickListener {
            openDateTimePicker()
        }
        mainView.etLocation.setOnClickListener {
            openMap()
        }

        observeViewModel()
        mainView.btnBook.setOnClickListener {
            lifecycleScope.launch {
                viewModel.bookApt()
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
        viewModel.bookAptRes.observe(viewLifecycleOwner, Observer { result ->
            when (result){
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    result.data?.let {
                        Toast.makeText(requireContext(), getString(R.string.apt_booked), Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    result.message?.let { msg ->
                        showDialog(msg)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.spinkitView)
                }
            }
        })
    }

    private fun openDateTimePicker() {
        // Get current date and time
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Create date picker dialog
        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Create time picker dialog
                val timePickerDialog = TimePickerDialog(requireContext(),
                    { _, selectedHourOfDay, selectedMinute ->
                        // Handle selected date and time
                        val selectedDateTime = Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, selectedDayOfMonth, selectedHourOfDay, selectedMinute)
                        }.timeInMillis
                        val formattedDateTime = SimpleDateFormat("dd/MM/yyyy 'at' HH:mm", Locale.getDefault())
                            .format(selectedDateTime)
                        mainView.etDatetime.setText(formattedDateTime)
                    },
                    hourOfDay, minute, true)
                timePickerDialog.show()
            },
            year, month, dayOfMonth)
        datePickerDialog.show()
    }

    private fun setData(sp: User) {
        Glide.with(requireContext()).load(Constants.IMG_URL +sp.pic).into(mainView.profileLayout.ivPic)
        mainView.profileLayout.tvFullname.text = sp.firstname + " " + sp.lastname
        mainView.profileLayout.tvSpeciality.text = sp.speciality
        mainView.profileLayout.tvAddress.text = sp.address
        mainView.profileLayout.tvRate.text = sp.rate.toString()
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

    private fun editTextsSetup() {
        mainView.etDatetime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setDate(convertDateString(s.toString()))
                viewModel.removeDateError()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        mainView.etLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setLocation(s.toString())
                viewModel.removeLocationError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        mainView.etDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setDesc(s.toString())
                viewModel.removeDescError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
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


    private fun inputsErrorHandling() {
        viewModel.dateError.observe(viewLifecycleOwner) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlDatetime.apply {
                    error = viewModel.dateError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlDatetime.apply {
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
        viewModel.tosError.observe(viewLifecycleOwner) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.llTosError.visibility=View.VISIBLE
            }else{
                mainView.llTosError.visibility=View.GONE
            }
        }
        viewModel.descError.observe(viewLifecycleOwner) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlDesc.apply {
                    error = viewModel.descError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlDesc.apply {
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

                mainView.etLocation.setText(location)
                viewModel.setLocation(location)

            }
        }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}


