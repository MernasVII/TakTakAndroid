package tn.esprit.taktakandroid.uis.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import kotlinx.coroutines.launch
import java.text.ParseException
import java.util.*
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentAptDetailsBinding
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.UpdateAptStateRequest
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.repositories.PaymentRepository
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModel
import tn.esprit.taktakandroid.uis.common.apts.AptsViewModelFactory
import tn.esprit.taktakandroid.uis.common.aptspending.PendingAptsViewModel
import tn.esprit.taktakandroid.uis.common.aptspending.PendingAptsViewModelFactory
import tn.esprit.taktakandroid.uis.common.payment.PaymentViewModel
import tn.esprit.taktakandroid.uis.common.payment.PaymentViewModelFactory
import tn.esprit.taktakandroid.uis.sp.sheets.AptPriceSheet
import tn.esprit.taktakandroid.uis.sp.sheets.QRCodeSheet
import tn.esprit.taktakandroid.uis.sp.sheets.PostponeAptSheet
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource
import java.text.SimpleDateFormat

class AptDetailsFragment : BaseFragment() {
    private val TAG = "AptDetailsFragment"

    private lateinit var mainView: FragmentAptDetailsBinding
    lateinit var viewModel: AptsViewModel
    lateinit var pendingAptsViewModel: PendingAptsViewModel
    private lateinit var paymentViewModel: PaymentViewModel

    val scanQrCodeLauncher = registerForActivityResult(ScanQRCode()) { result ->
        when(result){
            is QRResult.QRSuccess ->{
                val url = result.content.rawValue
                openLinkInBrowser(url)
            }
            is QRResult.QRUserCanceled ->{
            }
            is QRResult.QRMissingPermission ->{
                Toast.makeText(requireContext(), "Camera permission is required!", Toast.LENGTH_LONG).show()
            }
            is QRResult.QRError ->{
                Toast.makeText(requireContext(), "Error encountered when opening Scanner!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openLinkInBrowser(url: String) {
        val urlIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(urlIntent)
    }

    private lateinit var apt: Appointment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentAptDetailsBinding.inflate(layoutInflater)
        val aptRepository = AptRepository()
        viewModel = ViewModelProvider(this, AptsViewModelFactory(aptRepository))[AptsViewModel::class.java]
        pendingAptsViewModel = ViewModelProvider(this, PendingAptsViewModelFactory(aptRepository))[PendingAptsViewModel::class.java]
        apt = arguments?.getParcelable<Appointment>("apt")!!

        calculateTimeLeft(apt.date)

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
                pendingAptsViewModel.declineApt(IdBodyRequest(apt._id!!))
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
            }
            if (apt.state == 2) {
                openQrCodeSheet()
            }
        }
        mainView.btnScan.setOnClickListener {
            scanQrCodeLauncher.launch(null)
        }

        return mainView.root
    }

    private fun openQrCodeSheet() {
        val paymentRepository = PaymentRepository()
        paymentViewModel = ViewModelProvider(this, PaymentViewModelFactory(paymentRepository,apt))[PaymentViewModel::class.java]
        paymentViewModel.initRes.observe(viewLifecycleOwner){ response ->
            when(response){
                is Resource.Success -> {
                    response.data?.let { myRequestsResponse ->
                        val qrCodeSheet = QRCodeSheet()
                        val args = Bundle()
                        args.putString("payUrl", myRequestsResponse.payUrl)
                        qrCodeSheet.arguments = args
                        qrCodeSheet.show(parentFragmentManager, "exampleBottomSheet")
                    }
                }
                is Resource.Error -> {
                    Log.d(TAG, "onCreateView: error")
                }
                is Resource.Loading -> {
                    Log.d(TAG, "onCreateView: loading")
                }
            }
        }
    }

    private fun updateState(apt: Appointment) {
        lifecycleScope.launch {
            viewModel.updateAptState(UpdateAptStateRequest(apt._id!!, ++apt.state))
            manageAptStateBtn(apt.state)
        }
    }

    private fun isPastDate(apt: Appointment): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val dateTime = dateFormat.parse(apt.date.replace("Z", "+00:00"))
        val currentTime = Date()
        return !dateTime.after(currentTime)
    }


    private fun setData(apt: Appointment) {
        setTimeLeft(apt)
        mainView.tvDatetime.text = getTime(apt.date)
        mainView.tvLocation.text = apt.location
        mainView.tvTos.text = apt.tos
        mainView.tvDesc.text = apt.desc
        lifecycleScope.launch {
            val cin = AppDataStore.readString(Constants.CIN)
            manageViewsVisibiltiy(apt, cin)
            val user: User
            if (cin.isNullOrEmpty()) {
                user = apt.sp
                mainView.profileLayout.tvSpeciality.visibility = View.VISIBLE
                mainView.profileLayout.llRate.visibility = View.VISIBLE
                mainView.profileLayout.tvSpeciality.text = user.speciality
                mainView.profileLayout.tvRate.text = user.rate.toString()
            } else {
                user = apt.customer
                mainView.profileLayout.tvSpeciality.visibility = View.GONE
                mainView.profileLayout.llRate.visibility = View.GONE
            }
            Glide.with(requireContext()).load(Constants.IMG_URL + user.pic)
                .into(mainView.profileLayout.ivPic)
            mainView.profileLayout.tvFullname.text = user.firstname + " " + user.lastname
            mainView.profileLayout.tvAddress.text = user.address
        }
    }

    private fun setTimeLeft(apt: Appointment) {
        var timeLeftMs = calculateTimeLeft(apt.date)
        if (timeLeftMs!! > 0) {
            mainView.tvTimeLeft.visibility = View.VISIBLE
            mainView.btnPostpone.visibility = View.VISIBLE
            object : CountDownTimer(timeLeftMs, 1000) {
                //fired every 1 second
                override fun onTick(millisUntilFinished: Long) {
                    val timeLeftString = formatTimeLeft(millisUntilFinished)
                    mainView.tvTimeLeft.text = timeLeftString
                }

                // when the time is up
                override fun onFinish() {
                    mainView.tvTimeLeft.visibility = View.GONE
                    mainView.btnPostpone.visibility = View.GONE
                    mainView.btnState.visibility = View.VISIBLE
                }
            }.start()
        } else {
            mainView.tvTimeLeft.visibility = View.GONE
            mainView.btnPostpone.visibility = View.GONE
            mainView.btnState.visibility = View.VISIBLE
        }
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

    fun calculateTimeLeft(dateTimeString: String): Long? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
        return try {
            val dateTime = dateFormat.parse(dateTimeString)
            val now = Date()
            val duration = dateTime.time - now.time
            duration
        } catch (e: ParseException) {
            null
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
                    mainView.tvTimeLeft.visibility=View.GONE
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
                if (apt.state == 2) {
                    mainView.btnScan.visibility = View.VISIBLE
                } else {
                    mainView.btnScan.visibility = View.GONE
                }
            } else {
                mainView.btnScan.visibility = View.GONE
                mainView.llActive.visibility = View.VISIBLE
                if (apt.isArchived) {
                    mainView.tvTimeLeft.visibility=View.GONE
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