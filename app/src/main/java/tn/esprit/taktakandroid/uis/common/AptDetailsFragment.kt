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
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.uis.common.sheets.chat.ChatSheet
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
import tn.esprit.taktakandroid.uis.customer.sheets.RateSheet
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
    private var timer: CountDownTimer? = null


    private lateinit var apt: Appointment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentAptDetailsBinding.inflate(layoutInflater)
        val aptRepository = AptRepository()
        viewModel =
            ViewModelProvider(this, AptsViewModelFactory(aptRepository))[AptsViewModel::class.java]
        pendingAptsViewModel = ViewModelProvider(
            this,
            PendingAptsViewModelFactory(aptRepository)
        )[PendingAptsViewModel::class.java]
        apt = arguments?.getParcelable<Appointment>("apt")!!

        setData(apt!!)
        observeGetApt()
        observePostponeApt()
        observeCancelAptViewModel()
        observeDeclineAptViewModel()
        observeAcceptApt()

        mainView.profileLayout.ivChat.setOnClickListener {
            openChatSheet()
        }

        mainView.btnAccept.setOnClickListener {
            /*lifecycleScope.launch {
                viewModel.acceptApt(IdBodyRequest(apt._id!!))
            }*/
            val aptPriceSheet = AptPriceSheet()
            val args = Bundle()
            args.putString("aptId", apt._id)
            args.putString("customerID", apt.customer._id)
            aptPriceSheet.arguments = args
            aptPriceSheet.show(parentFragmentManager, "exampleBottomSheet")
        }
        mainView.btnDecline.setOnClickListener {
            showChoiceDialog ("Are you sure you want to decline this appointment?"){ pendingAptsViewModel.declineApt(IdBodyRequest(apt._id!!), apt.customer._id!!) }
        }
        mainView.btnCancel.setOnClickListener {

            showChoiceDialog ("Are you sure you want to cancel this appointment?"){viewModel.cancelApt(IdBodyRequest(apt._id!!), apt.sp._id!!)}

        }
        mainView.btnPostpone.setOnClickListener {
            val postponeAptSheet = PostponeAptSheet()
            val args = Bundle()
            args.putString("aptId", apt._id)
            args.putString("customerID", apt.customer._id)
            postponeAptSheet.arguments = args
            postponeAptSheet.show(parentFragmentManager, "exampleBottomSheet")
        }

        //state==0 initial; state==1: sp is here; state==2 waiting for payment/done
        mainView.btnState.setOnClickListener {
            if (apt.state < 2) {
                updateState(apt)
            }
            if (apt.state == 2) {
                openQrCodeSheet()
            }
        }
        mainView.btnScan.setOnClickListener {
            openRateSheet()
        }
        swipeLayoutSetup()

        return mainView.root
    }

    private fun openRateSheet() {
        val rateSheet = RateSheet()
        val args = Bundle()
        args.putString("aptId", apt._id)
        rateSheet.arguments = args
        rateSheet.show(parentFragmentManager, "exampleBottomSheet")
    }

    private fun openChatSheet() {
        val chatSheet = ChatSheet()
        val args = Bundle()
        args.putString("aptId", apt._id)
        var cin:String?=""
        lifecycleScope.launch {
            cin = AppDataStore.readString(Constants.CIN)
        }
        if(cin.isNullOrEmpty()){
            args.putParcelable("user", apt.sp)
        }else{
            args.putParcelable("user", apt.customer)
        }
        chatSheet.arguments = args
        chatSheet.show(parentFragmentManager, "exampleBottomSheet")
    }

    private fun observeAcceptApt() {
        parentFragmentManager.setFragmentResultListener(
            Constants.ACCEPTED_APT_RESULT,
            viewLifecycleOwner
        ) { _, _ ->
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun observePostponeApt() {
        parentFragmentManager.setFragmentResultListener(
            Constants.POSTPONED_RESULT,
            viewLifecycleOwner
        ) { _, _ ->
            viewModel.getApt(IdBodyRequest(apt._id!!))
        }
    }

    private fun openQrCodeSheet() {
        val paymentRepository = PaymentRepository()
        paymentViewModel = ViewModelProvider(
            this,
            PaymentViewModelFactory(paymentRepository, apt)
        )[PaymentViewModel::class.java]
        paymentViewModel.initRes.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { myRequestsResponse ->
                        val qrCodeSheet = QRCodeSheet()
                        val args = Bundle()
                        args.putString("payUrl", myRequestsResponse.payUrl)
                        args.putParcelable("apt", apt)
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
        mainView.tvDatetime.text = getTime(apt.date)
        mainView.tvLocation.text = apt.location
        mainView.tvTos.text = apt.tos
        mainView.tvDesc.text = apt.desc
        lifecycleScope.launch {
            val cin = AppDataStore.readString(Constants.CIN)
            setTimeLeft(apt, cin)
            manageViewsVisibiltiy(apt, cin)
            val user: User
            if (cin.isNullOrEmpty()) {
                user = apt.sp
                mainView.profileLayout.tvSpeciality.visibility = View.VISIBLE
                mainView.profileLayout.llRate.visibility = View.VISIBLE
                mainView.profileLayout.tvSpeciality.text = user.speciality
                mainView.profileLayout.tvRate.text = String.format("%.1f", user.rate)
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

    private fun setTimeLeft(apt: Appointment, cin: String?) {
        var timeLeftMs = calculateTimeLeft(apt.date)
        if (timeLeftMs!! > 0) {
            mainView.tvTimeLeft.visibility = View.VISIBLE
            mainView.btnPostpone.visibility = View.VISIBLE

            // check if the timer is running and cancel it
            if (timer != null) {
                timer?.cancel()
                timer = null
            }

            timer = object : CountDownTimer(timeLeftMs, 1000) {
                //fired every 1 second
                override fun onTick(millisUntilFinished: Long) {
                    val timeLeftString = formatTimeLeft(millisUntilFinished)
                    mainView.tvTimeLeft.text = timeLeftString
                }

                // when the time is up
                override fun onFinish() {
                    mainView.llActive.visibility = View.GONE
                    if (!cin.isNullOrEmpty()) {
                        mainView.btnState.visibility = View.VISIBLE
                    }
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
        if(apt.isAccepted && !apt.isArchived){
            mainView.profileLayout.ivChat.visibility=View.VISIBLE
        }else{
            mainView.profileLayout.ivChat.visibility=View.GONE
        }
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
                    mainView.tvTimeLeft.visibility = View.VISIBLE
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
                    mainView.tvTimeLeft.visibility = View.GONE
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

    private fun observeGetApt() {
        viewModel.getAptRes.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    mainView.scrollView.visibility = View.VISIBLE
                    response.data?.let { getAptResponse ->
                        setData(getAptResponse.apt)
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    mainView.swipeRefreshLayout.isRefreshing = false
                    mainView.scrollView.visibility = View.VISIBLE
                    response.message?.let { message ->
                        showDialog(message)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.spinkitView)
                    mainView.scrollView.visibility = View.GONE
                }
            }
        })
    }

    private fun observeCancelAptViewModel() {
        viewModel.cancelAptRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.data?.let {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.apt_canceled),
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().supportFragmentManager.popBackStack()
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

    private fun observeDeclineAptViewModel() {
        pendingAptsViewModel.declineAptRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.spinkitView)
                    result.data?.let {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.apt_canceled),
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().supportFragmentManager.popBackStack()
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

    fun swipeLayoutSetup() {
        mainView.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.orangeToBG,
                null
            )
        )
        mainView.swipeRefreshLayout.setOnRefreshListener {
            if (mainView.spinkitView.visibility != View.VISIBLE) {
                viewModel.getApt(IdBodyRequest(apt._id!!))
            } else {
                mainView.swipeRefreshLayout.isRefreshing = false

            }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getApt(IdBodyRequest(apt._id!!))
    }


}