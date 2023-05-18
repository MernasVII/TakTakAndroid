package tn.esprit.taktakandroid.uis.common.registerTwo

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.SparseArray
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.button.MaterialButton
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import render.animations.Attention
import render.animations.Render
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ActivityRegisterTwoBinding
import tn.esprit.taktakandroid.databinding.LayoutDialogBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.BaseActivity
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "RegisterTwoActivity"

class RegisterTwoActivity : BaseActivity() {
    private lateinit var mainView: ActivityRegisterTwoBinding
    private var imageBitmap: Bitmap? = null
    private lateinit var recognizer: TextRecognizer
    private lateinit var viewModel: RegisterTwoViewModel
    private lateinit var tosButtons: List<MaterialButton>
    private lateinit var workDaysButtons: List<MaterialButton>
    private lateinit var render: Render

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = ActivityRegisterTwoBinding.inflate(layoutInflater)
        setContentView(mainView.root)
        render=Render(this)


        val userRepository = UserRepository()
        val viewModelProviderFactory = RegisterTwoViewModelProviderFactory(userRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[RegisterTwoViewModel::class.java]
        recognizer = TextRecognizer.Builder(this).build()

        buttonsSetup()
        editTextsSetup()
        getDataFromPreviousUi()
        inputsErrorHandling()



        mainView.btnRegSp.setOnClickListener {
            //TODO remove this line because cin value will be set from ocr
            viewModel.setCin(mainView.etCin.text.toString())
            viewModel.signUp()

        }

        mainView.etCin.apply {
            setOnClickListener {
                showGuideDialog()
            }
         //  isLongClickable=false //TODO enable comment
        }

        viewModel.signUpResult.observe(this){ result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.progressBar)
                    result.data?.let {
                        lifecycleScope.launch {
                            showSnackbar(it.message, mainView.cl)
                            delay(1000L)
                            setResult(RESULT_OK)
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


    }

    private fun editTextsSetup() {
        mainView.etSpeciality.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSpeciality(s.toString())
                viewModel.removeSpecialityError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        mainView.etCin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setCin(s.toString())
                viewModel.removeCinError()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun getDataFromPreviousUi() {
        intent.apply {
            viewModel.setEmail(getStringExtra("email")!!)
            viewModel.setFirstname(getStringExtra("firstname")!!)
            viewModel.setLastname(getStringExtra("lastname")!!)
            viewModel.setAddress(getStringExtra("address")!!)
            viewModel.setPassword(getStringExtra("password")!!)
        }
    }

    private fun buttonsSetup() {
        tosButtons = listOf(
            mainView.btnInstallation, mainView.btnMaintenance, mainView.btnRepair
        )
        tosButtons.forEach {
            handleTosClicks(it)
        }


        workDaysButtons = listOf(
            mainView.btnMonday,
            mainView.btnTuesday,
            mainView.btnWednesday,
            mainView.btnThursday,
            mainView.btnFriday,
            mainView.btnSaturday,
        )
        workDaysButtons.forEach {
            handleWorkDaysClicks(it)
        }
    }

    private fun handleTosClicks(btn: MaterialButton) {
        btn.setOnClickListener {
            if (viewModel.tos.value!!.contains(btn.text.toString())) {
                viewModel.deleteTos(btn.text.toString())
                btn.setBackgroundColor(getColor(R.color.white))
                btn.strokeColor = ColorStateList.valueOf(getColor(R.color.BGToOrange))
                btn.setTextColor(getColor(R.color.BGToOrange))

            } else {
                viewModel.addTos(btn.text.toString())
                btn.setBackgroundColor(getColor(R.color.BGToOrange))
                btn.setTextColor(getColor(R.color.white))
            }
        }

    }

    private fun handleWorkDaysClicks(btn: MaterialButton) {
        btn.setOnClickListener {
            if (viewModel.workDays.value!!.contains(btn.text.toString())) {
                viewModel.deleteDay(btn.text.toString())
                btn.setBackgroundColor(getColor(R.color.white))
                btn.strokeColor = ColorStateList.valueOf(getColor(R.color.BGToOrange))
                btn.setTextColor(getColor(R.color.BGToOrange))

            } else {
                viewModel.addDay(btn.text.toString())
                btn.setBackgroundColor(getColor(R.color.BGToOrange))
                btn.setTextColor(getColor(R.color.white))
            }
        }

    }


    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val uri: Uri = data?.data!!

                val bitmap = MediaStore.Images.Media.getBitmap(
                    this.contentResolver, uri
                )

                imageBitmap = bitmap
                processImage(bitmap)


            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
        }

    private fun processImage(bitmap: Bitmap?) {
        val frame = Frame.Builder().setBitmap(bitmap!!).build()
        val tb = recognizer.detect(frame) as SparseArray<TextBlock>
        val result = StringBuilder();
        for (i in 0 until tb.size()) {
            if (tb.valueAt(i).value.toString().trim().length == 8) {
                result.append(tb.valueAt(i).value)
            } else {
                Toast.makeText(this, getString(R.string.op_failed), Toast.LENGTH_SHORT).show()

            }
        }
        mainView.etCin.setText(result.toString())
        viewModel.setCin(result.toString())

    }

    private fun showGuideDialog() {
        val builder = AlertDialog.Builder(this)
        val binding = LayoutDialogBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.tvTitle.text = getString(R.string.scan_guide)
        binding.tvMessage.text = getString(R.string.scan_help_sentence)
        binding.iv.visibility = View.VISIBLE
        binding.tvBtn.setOnClickListener {
            dialog.dismiss()
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
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun inputsErrorHandling() {
        viewModel.specialityError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlSpeciality.apply {
                    error = viewModel.specialityError.value
                    isErrorEnabled = true
                    render.setAnimation(Attention.Shake(mainView.tlSpeciality))
                    render.start()
                }
            } else {
                mainView.tlSpeciality.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.cinError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlCin.apply {
                    error = viewModel.cinError.value
                    isErrorEnabled = true
                    render.setAnimation(Attention.Shake(mainView.tlCin))
                    render.start()
                }
            } else {
                mainView.tlCin.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.tosError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                showSnackbar(_errorTxt, mainView.cl)
            }
        }
        viewModel.workDaysError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                showSnackbar(_errorTxt, mainView.cl)
            }
        }

    }

}