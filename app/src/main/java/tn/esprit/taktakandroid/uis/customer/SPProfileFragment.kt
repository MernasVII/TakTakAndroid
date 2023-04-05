package tn.esprit.taktakandroid.uis.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentSpProfileBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.uis.HomeViewModel
import tn.esprit.taktakandroid.uis.common.HomeActivity
import tn.esprit.taktakandroid.utils.Constants

class SPProfileFragment : Fragment(R.layout.fragment_sp_profile) {
    private val TAG="SPProfileFragment"
    lateinit var viewModel: HomeViewModel
    private lateinit var mainView: FragmentSpProfileBinding
    private lateinit var tosButtons: List<MaterialButton>
    private lateinit var workDaysButtons: List<MaterialButton>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView=FragmentSpProfileBinding.inflate(layoutInflater)
        viewModel=(activity as HomeActivity).viewModel
        buttonsSetup()
        val user = arguments?.getParcelable<User>("user")
        if (user != null) {
            mainView.profileLayout.tvFullname.text=user.firstname+" "+user.lastname
            mainView.profileLayout.tvAddress.text=user.address
            mainView.profileLayout.tvRate.text= String.format("%.1f", user.rate)
            Glide.with(this).load(Constants.IMG_URL +user.pic).into(mainView.profileLayout.ivPic)

        } else {
            // Handle error
        }
        return mainView.root
    }

    private fun buttonsSetup() {
        tosButtons = listOf(
            mainView.btnInstallation, mainView.btnMaintenance, mainView.btnRepair
        )
        tosButtons.forEach {
            //handleTosClicks(it)
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
            //handleWorkDaysClicks(it)
        }
    }




}