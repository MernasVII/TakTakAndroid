package tn.esprit.taktakandroid.uis.sp.sheets.updatework

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentUpdateWorkDescriptionBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.SheetBaseFragment
import tn.esprit.taktakandroid.utils.Resource


class UpdateWorkDescriptionSheet (private val user: User) : SheetBaseFragment() {
    private val TAG="UpdateWorkDescriptionSheet"

    lateinit var viewModel: UpdateWorkDescriptionViewModel
    private lateinit var mainView: SheetFragmentUpdateWorkDescriptionBinding

    private lateinit var tosButtons: List<MaterialButton>
    private lateinit var workDaysButtons: List<MaterialButton>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentUpdateWorkDescriptionBinding.inflate(layoutInflater, container, false)
        val userRepository = UserRepository()
        viewModel = ViewModelProvider(this,UpdateWorkDescriptionViewModelFactory(userRepository))[UpdateWorkDescriptionViewModel::class.java]

        setupSheetBehavior()
        buttonsSetup()
        initGridButtons()
        editTextsSetup()
        inputsErrorHandling()

        setData()


        observeViewModel()
        mainView.btnSaveChanges.setOnClickListener {
            lifecycleScope.launch {
                viewModel.updateWorkDesc()
            }
        }

        return mainView.root
    }

    private fun observeViewModel() {
        viewModel.updateWorkDescRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    result.data?.let {
                        Toast.makeText(requireContext(), getString(R.string.work_desc_updated), Toast.LENGTH_SHORT).show()
                        dismiss()
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


    private fun setData() {
        mainView.etSpeciality.setText(user.speciality)
        user.tos?.forEach { tos ->
            viewModel.addTos(tos)
        }
        user.workDays?.forEach { workDay ->
            viewModel.addDay(workDay)
        }
    }

    private fun initGridButtons(){
        viewModel.tos.observe(this){ tos ->
            Log.d(TAG, "observe: ${viewModel.tos.value}")
            if(!tos.isNullOrEmpty()){
                tosButtons.forEach {
                    if(tos.contains(it.text))
                        selectBtn(it)
                }
            }
        }
        viewModel.workDays.observe(this){ workDays ->
            if(!workDays.isNullOrEmpty()){
                workDaysButtons.forEach {
                    if(workDays.contains(it.text))
                        selectBtn(it)
                }
            }
        }
    }

    private fun setupSheetBehavior() {
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = STATE_EXPANDED
            isDraggable = false
        }
    }

    private fun editTextsSetup() {
        val currentSpeciality = mainView.etSpeciality.text?.toString()?.trim()
        if (currentSpeciality != null) {
            viewModel.setSpeciality(currentSpeciality)
        }
        mainView.etSpeciality.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSpeciality(s.toString())
                viewModel.removeSpecialityError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun buttonsSetup() {
        tosButtons = listOf(
            mainView.btnInstallation, mainView.btnMaintenance, mainView.btnRepair
        )
        tosButtons.forEach {
            it.setOnClickListener { _ ->
                colorTosBtns(it)
            }
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
            it.setOnClickListener { _ ->
                colorWorkDaysBtns(it)
            }
        }
    }

    private fun colorTosBtns(btn:MaterialButton){
        if (viewModel.tos.value!!.contains(btn.text.toString())) {
            viewModel.deleteTos(btn.text.toString())
            unselectBtn(btn)
        } else {
            viewModel.addTos(btn.text.toString())
            selectBtn(btn)
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

    private fun colorWorkDaysBtns(btn: MaterialButton) {
            if (viewModel.workDays.value!!.contains(btn.text.toString())) {
                viewModel.deleteDay(btn.text.toString())
                unselectBtn(btn)
            } else {
                viewModel.addDay(btn.text.toString())
                selectBtn(btn)
            }
    }

    private fun inputsErrorHandling() {
        viewModel.specialityError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlSpeciality.apply {
                    error = viewModel.specialityError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlSpeciality.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.tosError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.llTosError.visibility=View.VISIBLE
            }else{
                mainView.llTosError.visibility=View.GONE
            }
        }
        viewModel.workDaysError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.llWorkError.visibility=View.VISIBLE
            }else{
                mainView.llWorkError.visibility=View.GONE
            }
        }

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