package tn.esprit.taktakandroid.uis.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentSpProfileBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.customer.bookapt.BookAptFragment
import tn.esprit.taktakandroid.utils.Constants

class SPProfileFragment : BaseFragment() {
    private val TAG="SPProfileFragment"

    private lateinit var mainView: FragmentSpProfileBinding
    private lateinit var tosButtons: List<MaterialButton>
    private lateinit var workDaysButtons: List<MaterialButton>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView=FragmentSpProfileBinding.inflate(layoutInflater)

        val user = arguments?.getParcelable<User>("user")
        if (user != null) {
            setData(user)
            mainView.btnBook.setOnClickListener {
                navigateToBookApt(user)
            }
        } else {
            // Handle error
        }

        return mainView.root
    }

    private fun setData(user:User) {
        mainView.profileLayout.tvFullname.text=user.firstname+" "+user.lastname
        mainView.profileLayout.tvAddress.text=user.address
        mainView.profileLayout.tvSpeciality.text=user.speciality
        mainView.profileLayout.tvRate.text= String.format("%.1f", user.rate)
        var userImage=Constants.IMG_URL + user.pic
        if(user.pic!!.lowercase().contains("http")) userImage = user.pic!!
        Glide.with(this).load(userImage).into(mainView.profileLayout.ivPic)
        initGridButtons(user)
    }

    private fun initGridButtons(user:User){
        tosButtons = listOf(
            mainView.btnInstallation, mainView.btnMaintenance, mainView.btnRepair
        )
        workDaysButtons = listOf(
            mainView.btnMonday,
            mainView.btnTuesday,
            mainView.btnWednesday,
            mainView.btnThursday,
            mainView.btnFriday,
            mainView.btnSaturday,
        )
        if (!user.tos.isNullOrEmpty()) {
            tosButtons.forEach {
                if (user.tos.contains(it.text))
                    selectBtn(it)
            }
        }

        if (!user.workDays.isNullOrEmpty()) {
            workDaysButtons.forEach {
                if (user.workDays.contains(it.text))
                    selectBtn(it)
            }
        }
    }

    private fun selectBtn(btn: MaterialButton) {
        btn.setBackgroundColor(requireActivity().getColor(R.color.BGToLB))
        btn.setTextColor(requireActivity().getColor(R.color.white))
    }

    private fun navigateToBookApt(sp:User) {
        val bookAptFragment= BookAptFragment()
        val bundle = Bundle().apply {
            putParcelable("sp", sp)
        }
        bookAptFragment.arguments = bundle
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, bookAptFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()

    }

}