package tn.esprit.taktakandroid.uis.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.customer.myRequests.CustomerReqsFragment
import tn.esprit.taktakandroid.uis.sp.spRequests.SPReqsFragment
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.databinding.ActivityHomeBinding
import tn.esprit.taktakandroid.uis.BaseActivity
import tn.esprit.taktakandroid.uis.common.apts.AptsFragment
import tn.esprit.taktakandroid.uis.common.notifs.NotifsFragment
import tn.esprit.taktakandroid.uis.common.userprofile.UserProfileFragment
import tn.esprit.taktakandroid.uis.common.userprofile.UserProfileViewModel
import tn.esprit.taktakandroid.uis.common.userprofile.UserProfileViewModelFactory
import tn.esprit.taktakandroid.uis.customer.spslist.SPsFragment
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource
import tn.esprit.taktakandroid.utils.SocketService

class HomeActivity  : BaseActivity() {
    private lateinit var mainView : ActivityHomeBinding
    private val TAG="HomeActivity"

    private val spsFragment = SPsFragment()
    private val aptsFragment = AptsFragment()
    private val customerReqsFragment = CustomerReqsFragment()
    private val spReqsFragment = SPReqsFragment()
    private val notifsFragment = NotifsFragment()
    private val profileFragment = UserProfileFragment()

    private var cin:String? = null

    lateinit var uservm: UserProfileViewModel

    val viewModel: HomeViewModel by viewModels {
        HomeViewModelProviderFactory(UserRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, SocketService::class.java)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        startService(intent)
        mainView=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mainView.root)
        val userRepository = UserRepository()
        uservm = ViewModelProvider(this, UserProfileViewModelFactory(userRepository))[UserProfileViewModel::class.java]

        val initialFragmentCustomer = SPsFragment()
        val initialFragmentSP = AptsFragment()

        lifecycleScope.launch(Dispatchers.Main){
            cin = AppDataStore.readString(Constants.CIN)
            Log.d(TAG, "onCreate: $cin")
            if(!cin.isNullOrEmpty()){
                val bottomNav = findViewById<View>(R.id.bottom_navigation) as ViewGroup
                val providersImageView = bottomNav.findViewById<View>(R.id.tv_providers)
                bottomNav.removeView(providersImageView)
                replaceFragment(initialFragmentSP)
                setIconsColros(mainView.bottomNavigation.tvApts)
            }else{
                replaceFragment(initialFragmentCustomer)
                setIconsColros(mainView.bottomNavigation.tvProviders)
            }
        }



        mainView.bottomNavigation.tvProviders.setOnClickListener {
            replaceFragment(spsFragment)
            setIconsColros(mainView.bottomNavigation.tvProviders)
        }

        mainView.bottomNavigation.tvApts.setOnClickListener {
            replaceFragment(aptsFragment)
            setIconsColros(mainView.bottomNavigation.tvApts)
        }

        mainView.bottomNavigation.tvReqs.setOnClickListener {
            setIconsColros(mainView.bottomNavigation.tvReqs)
            if(cin.isNullOrEmpty()){
                replaceFragment(customerReqsFragment)
            }else{
                replaceFragment(spReqsFragment)
            }
        }

        mainView.bottomNavigation.tvNotifs.setOnClickListener {
            replaceFragment(notifsFragment)
            setIconsColros(mainView.bottomNavigation.tvNotifs)
        }

        mainView.bottomNavigation.tvProfile.setOnClickListener {
            replaceFragment(profileFragment)
            setIconsColros(mainView.bottomNavigation.tvProfile)
        }

        checkAddress()

    }

    private fun setIconsColros(imageView: ImageView) {
        mainView.bottomNavigation.tvProviders.setColorFilter(ContextCompat.getColor(this, if (imageView == mainView.bottomNavigation.tvProviders) R.color.yellow else R.color.white))
        mainView.bottomNavigation.tvApts.setColorFilter(ContextCompat.getColor(this, if (imageView == mainView.bottomNavigation.tvApts) R.color.yellow else R.color.white))
        mainView.bottomNavigation.tvReqs.setColorFilter(ContextCompat.getColor(this, if (imageView == mainView.bottomNavigation.tvReqs) R.color.yellow else R.color.white))
        mainView.bottomNavigation.tvNotifs.setColorFilter(ContextCompat.getColor(this, if (imageView == mainView.bottomNavigation.tvNotifs) R.color.yellow else R.color.white))
        mainView.bottomNavigation.tvProfile.setColorFilter(ContextCompat.getColor(this, if (imageView == mainView.bottomNavigation.tvProfile) R.color.yellow else R.color.white))
    }

    private fun checkAddress() {
        uservm.userProfileRes.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { userProfileResponse ->
                        val user = userProfileResponse.user
                        if(user.address.isNullOrEmpty()){
                            mainView.bottomNavigation.ivErrorAddress.visibility=View.VISIBLE
                        }else{
                            mainView.bottomNavigation.ivErrorAddress.visibility=View.GONE
                        }

                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        showDialog(message)
                    }
                }
                is Resource.Loading -> {
                    Log.d(TAG, "getUser: Loading")
                }
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}