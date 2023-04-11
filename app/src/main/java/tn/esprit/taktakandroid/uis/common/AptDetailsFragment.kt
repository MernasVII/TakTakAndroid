package tn.esprit.taktakandroid.uis.common

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentAptDetailsBinding
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.UpdateAptStateRequest
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModelFactory
import tn.esprit.taktakandroid.uis.sp.sheets.AptPriceSheet
import tn.esprit.taktakandroid.uis.sp.sheets.QRCodeSheet
import tn.esprit.taktakandroid.uis.sp.sheets.PostponeAptSheet
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource
import java.text.SimpleDateFormat
import java.util.*

class AptDetailsFragment : BaseFragment() {
    private val TAG = "AptDetailsFragment"

    private lateinit var mainView: FragmentAptDetailsBinding
    lateinit var viewModel: AptsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentAptDetailsBinding.inflate(layoutInflater)
        val aptRepository = AptRepository()
        viewModel =
            ViewModelProvider(this, AptsViewModelFactory(aptRepository))[AptsViewModel::class.java]
        val apt = arguments?.getParcelable<Appointment>("apt")

        setData(apt!!)

        mainView.btnAccept.setOnClickListener {
            /*lifecycleScope.launch {
                viewModel.acceptApt(IdBodyRequest(apt._id!!))
            }*/
            val aptPriceSheet = AptPriceSheet()
            val args = Bundle()
            args.putString("aptId", apt._id)
            aptPriceSheet.arguments = args
            aptPriceSheet.show(parentFragmentManager, "exampleBottomSheet")
        }
        mainView.btnDecline.setOnClickListener {
            lifecycleScope.launch {
                viewModel.declineApt(IdBodyRequest(apt._id!!))
            }
        }
        mainView.btnCancel.setOnClickListener {
            lifecycleScope.launch {
                viewModel.cancelApt(IdBodyRequest(apt._id!!))
            }
        }
        mainView.btnPostpone.setOnClickListener {
            val postponeAptSheet = PostponeAptSheet()
            val args = Bundle()
            args.putString("aptId", apt._id)
            postponeAptSheet.arguments = args
            postponeAptSheet.show(parentFragmentManager, "exampleBottomSheet")
        }
        mainView.btnState.setOnClickListener {
            if (apt.state < 2) {
                updateState(apt)
            } else if (apt.state == 2) {
                val qrCodeSheet = QRCodeSheet()
                val args = Bundle()
                args.putString("aptKey", apt._id)
                qrCodeSheet.arguments = args
                qrCodeSheet.show(parentFragmentManager, "exampleBottomSheet")
            }
        }
        mainView.btnScan.setOnClickListener {
            //TODO OPEN QR CODE SCANNER
        }

        return mainView.root
    }

    private fun updateState(apt: Appointment) {
        lifecycleScope.launch {
            viewModel.updateAptState(UpdateAptStateRequest(apt._id!!, ++apt.state))
            manageAptStateBtn(apt.state)
        }
    }

    private fun observeViewModel(apt: Appointment) {
        // Observe the LiveData in the ViewModel
        viewModel.timeLeftAptRes.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Handle loading state
                }
                is Resource.Success -> {
                    // Log the result response
                    val timeLeftString=formatTimeLeft(resource.data!!.timeLeft)
                    if(timeLeftString.trim()!="0"){
                        mainView.tvTimeLeft.visibility=View.VISIBLE
                        mainView.tvTimeLeft.text= formatTimeLeft(resource.data!!.timeLeft)
                    }else{
                        mainView.tvTimeLeft.visibility=View.GONE
                    }
                    // Handle success state
                }
                is Resource.Error -> {
                    // Handle error state
                    Log.d("MyFragment", "error")
                }
            }
        }
        // Call the method in the ViewModel to fetch the data
        viewModel.getTimeLeftToApt(IdBodyRequest(apt._id!!))
    }

    private fun isPastDate(apt: Appointment): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val dateTime = dateFormat.parse(apt.date.replace("Z", "+00:00"))
        val currentTime = Date()
        return !dateTime.after(currentTime)
    }

    fun formatTimeLeft(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        val remainingHours = hours % 24
        val remainingMinutes = minutes % 60
        val remainingSeconds = seconds % 60

        return "${days} Days, ${remainingHours} hours, ${remainingMinutes} minutes and ${remainingSeconds} seconds left"
    }


    private fun setData(apt: Appointment) {
        observeViewModel(apt)
        mainView.tvDatetime.text = getTime(apt.date)
        mainView.tvLocation.text = apt.location
        mainView.tvTos.text = apt.tos
        mainView.tvDesc.text = apt.desc
        lifecycleScope.launch {
            val cin = AppDataStore.readString(Constants.CIN)
            manageViewsVisibiltiy(apt, cin)
            if (cin.isNullOrEmpty()) {
                mainView.profileLayout.tvSpeciality.visibility = View.VISIBLE
                mainView.profileLayout.tvFullname.text = apt.sp.firstname + " " + apt.sp.lastname
                mainView.profileLayout.tvSpeciality.text = apt.sp.speciality
                mainView.profileLayout.tvAddress.text = apt.sp.address
            } else {
                mainView.profileLayout.tvSpeciality.visibility = View.GONE
                mainView.profileLayout.tvFullname.text =
                    apt.customer.firstname + " " + apt.customer.lastname
                mainView.profileLayout.tvAddress.text = apt.customer.address
            }
        }
    }

    private fun manageViewsVisibiltiy(apt: Appointment, cin: String?) {
        if (!cin.isNullOrEmpty()) {
            mainView.btnCancel.visibility = View.GONE
            mainView.btnScan.visibility = View.GONE
            if (isPastDate(apt)) {
                mainView.llActive.visibility = View.GONE
                mainView.btnState.visibility = View.VISIBLE
                manageAptStateBtn(apt.state)
            } else {
                mainView.btnState.visibility = View.GONE
                if (apt.isArchived) {
                    mainView.llActive.visibility = View.GONE
                } else if (apt.isAccepted) {
                    mainView.llActive.visibility = View.VISIBLE
                    mainView.btnPostpone.visibility = View.VISIBLE
                    mainView.llPendingSp.visibility = View.GONE
                } else {
                    mainView.llActive.visibility = View.VISIBLE
                    mainView.btnPostpone.visibility = View.GONE
                    mainView.llPendingSp.visibility = View.VISIBLE
                }
            }
        } else {
            mainView.btnState.visibility = View.GONE
            mainView.btnPostpone.visibility = View.GONE
            mainView.llPendingSp.visibility = View.GONE
            if (isPastDate(apt)) {
                mainView.llActive.visibility = View.GONE
                if (apt.state == 3) {
                    mainView.btnScan.visibility = View.VISIBLE
                } else {
                    mainView.btnScan.visibility = View.GONE
                }
            } else {
                mainView.btnScan.visibility = View.GONE
                mainView.llActive.visibility = View.VISIBLE
                if (apt.isArchived) {
                    mainView.btnCancel.visibility = View.GONE
                } else {
                    mainView.btnCancel.visibility = View.VISIBLE
                }
            }


        }
    }

    private fun manageAptStateBtn(state: Int) {
        if (state == 0) {
            mainView.btnState.text = getString(R.string.here)
        } else {
            mainView.btnState.text = getString(R.string.generate_code)
        }
    }

    private fun getTime(dateString: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(dateString)

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        dateFormatter.timeZone = TimeZone.getDefault()
        val dateStr = dateFormatter.format(date)

        val timeFormatter = SimpleDateFormat("HH:mm")
        timeFormatter.timeZone = TimeZone.getDefault()
        val timeStr = timeFormatter.format(date)
        return "$dateStr at $timeStr"
    }

}