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
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentUpdateWorkDescriptionBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.UserRepository


class UpdateWorkDescriptionSheet (private val user: User) : BottomSheetDialogFragment() {
    private val TAG="UpdateWorkDescriptionSheet"

    lateinit var viewModel: UpdateWorkDescriptionViewModel
    private lateinit var mainView: SheetFragmentUpdateWorkDescriptionBinding

    private lateinit var tosButtons: List<MaterialButton>
    private lateinit var workDaysButtons: List<MaterialButton>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainView =
            SheetFragmentUpdateWorkDescriptionBinding.inflate(layoutInflater, container, false)
        val userRepository = UserRepository()
        viewModel = ViewModelProvider(
            this,
            UpdateWorkDescriptionViewModelFactory(userRepository)
        )[UpdateWorkDescriptionViewModel::class.java]
        setupSheetBehavior()

        buttonsSetup()
        initGridButtons()
        setData()

        editTextsSetup()
        inputsErrorHandling()

        mainView.btnSaveChanges.setOnClickListener {
            viewModel.updateWorkDesc()
        }

        return mainView.root
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
        btn.setBackgroundColor(requireActivity().getColor(R.color.orange))
        btn.setTextColor(requireActivity().getColor(R.color.white))
    }

    private fun unselectBtn(btn:MaterialButton) {
        btn.setBackgroundColor(requireActivity().getColor(R.color.white))
        btn.strokeColor = ColorStateList.valueOf(requireActivity().getColor(R.color.orange))
        btn.setTextColor(requireActivity().getColor(R.color.orange))
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
                Toast.makeText(requireContext(), "tosError", Toast.LENGTH_SHORT).show()
                //showSnackbar(_errorTxt, mainView.cl)
            }
        }
        viewModel.workDaysError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                Toast.makeText(requireContext(), "wrokDaysError", Toast.LENGTH_SHORT).show()
                //showSnackbar(_errorTxt, mainView.cl)
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