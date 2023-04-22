package tn.esprit.taktakandroid.uis.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.customer.myRequests.CustomerReqsFragment
import tn.esprit.taktakandroid.uis.sp.spRequests.SPReqsFragment
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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

class HomeActivity  : BaseActivity() {
    private lateinit var mainView : ActivityHomeBinding
    private val TAG="HomeActivity"

    private val spsFragment = SPsFragment()
    private val aptsFragment = AptsFragment()
    private val customerReqsFragment = CustomerReqsFragment()
    private val spReqsFragment = SPReqsFragment()
    private val notifsFragment = NotifsFragment()
    private val profileFragment = UserProfileFragment()

    lateinit var uservm: UserProfileViewModel

    val viewModel: HomeViewModel by viewModels {
        HomeViewModelProviderFactory(UserRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mainView.root)
        val userRepository = UserRepository()
        uservm = ViewModelProvider(this, UserProfileViewModelFactory(userRepository))[UserProfileViewModel::class.java]

        val initialFragment = SPsFragment()
        replaceFragment(initialFragment)

        mainView.bottomNavigation.tvProviders.setOnClickListener {
            replaceFragment(spsFragment)
        }

        mainView.bottomNavigation.tvApts.setOnClickListener {
            replaceFragment(aptsFragment)
        }

        lifecycleScope.launch (Dispatchers.IO) {
            val cin = AppDataStore.readString(Constants.CIN)
            Log.d(TAG, "onCreate: $cin")
            mainView.bottomNavigation.tvReqs.setOnClickListener {
                if(cin.isNullOrEmpty()){
                    replaceFragment(customerReqsFragment)
                }else{
                replaceFragment(spReqsFragment)
                }
            }
        }

        mainView.bottomNavigation.tvNotifs.setOnClickListener {
            replaceFragment(notifsFragment)
        }

        mainView.bottomNavigation.tvProfile.setOnClickListener {
            replaceFragment(profileFragment)
        }

        checkAddress()

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
