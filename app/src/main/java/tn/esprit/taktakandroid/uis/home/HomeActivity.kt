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
import androidx.appcompat.app.AppCompatDelegate
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
import tn.esprit.taktakandroid.uis.BaseFragment
import tn.esprit.taktakandroid.uis.common.apts.AptsFragment
import tn.esprit.taktakandroid.uis.common.login.LoginActivity
import tn.esprit.taktakandroid.uis.common.notifs.NotifsFragment
import tn.esprit.taktakandroid.uis.common.userprofile.UserProfileFragment
import tn.esprit.taktakandroid.uis.common.userprofile.UserProfileViewModel
import tn.esprit.taktakandroid.uis.common.userprofile.UserProfileViewModelFactory
import tn.esprit.taktakandroid.uis.customer.spslist.SPsFragment
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource
import tn.esprit.taktakandroid.utils.SocketService

private const val TAG = "HomeActivity"

class HomeActivity : BaseActivity() {
    private lateinit var mainView: ActivityHomeBinding
    private lateinit var initialFragment: BaseFragment

    private val spsFragment = SPsFragment()
    private val aptsFragment = AptsFragment()
    private val customerReqsFragment = CustomerReqsFragment()
    private val spReqsFragment = SPReqsFragment()
    private val notifsFragment = NotifsFragment()
    private val profileFragment = UserProfileFragment()

    private var cin: String? = null

    lateinit var uservm: UserProfileViewModel

    val viewModel: HomeViewModel by viewModels {
        HomeViewModelProviderFactory(UserRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val showNotif = intent.getBooleanExtra("showNotif", false)
        val intent = Intent(this, SocketService::class.java)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        startService(intent)
        ContextCompat.startForegroundService(this, intent)
        mainView = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mainView.root)
        val userRepository = UserRepository()
        uservm = ViewModelProvider(
            this,
            UserProfileViewModelFactory(userRepository)
        )[UserProfileViewModel::class.java]


        lifecycleScope.launch(Dispatchers.Main){
            cin = AppDataStore.readString(Constants.CIN)
            if (!cin.isNullOrEmpty()) {

                initialFragment = aptsFragment
                val bottomNav = findViewById<View>(R.id.bottom_navigation) as ViewGroup
                val providersImageView = bottomNav.findViewById<View>(R.id.tv_providers)
                bottomNav.removeView(providersImageView)
                setIconsColors(mainView.bottomNavigation.tvApts)
            } else {
                initialFragment = spsFragment
                setIconsColors(mainView.bottomNavigation.tvProviders)
            }
            if (showNotif) {
                initialFragment = notifsFragment
                setIconsColors(mainView.bottomNavigation.tvNotifs)
            }
            Log.d(TAG, "onCreate: showNotif $showNotif")

            replaceFragment(initialFragment)

        }



        mainView.bottomNavigation.tvProviders.setOnClickListener {
            replaceFragment(spsFragment)
            setIconsColors(mainView.bottomNavigation.tvProviders)
        }

        mainView.bottomNavigation.tvApts.setOnClickListener {
            replaceFragment(aptsFragment)
            setIconsColors(mainView.bottomNavigation.tvApts)
        }

        mainView.bottomNavigation.tvReqs.setOnClickListener {
            setIconsColors(mainView.bottomNavigation.tvReqs)
            if (cin.isNullOrEmpty()) {
                replaceFragment(customerReqsFragment)
            } else {
                replaceFragment(spReqsFragment)
            }
        }

        mainView.bottomNavigation.tvNotifs.setOnClickListener {
            replaceFragment(notifsFragment)
            setIconsColors(mainView.bottomNavigation.tvNotifs)
        }

        mainView.bottomNavigation.tvProfile.setOnClickListener {
            replaceFragment(profileFragment)
            setIconsColors(mainView.bottomNavigation.tvProfile)
        }

        checkAddress()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val showNotif = intent?.getBooleanExtra("showNotif", false)
        Log.d(TAG, "onNewIntent: showNotif $showNotif")

        if (showNotif!!) {
            replaceFragment(notifsFragment)
            setIconsColors(mainView.bottomNavigation.tvNotifs)

        }

    }

    private fun setIconsColors(imageView: ImageView) {
        mainView.bottomNavigation.tvProviders.setColorFilter(
            ContextCompat.getColor(
                this,
                if (imageView == mainView.bottomNavigation.tvProviders) R.color.nav_selected else R.color.white
            )
        )
        mainView.bottomNavigation.tvApts.setColorFilter(
            ContextCompat.getColor(
                this,
                if (imageView == mainView.bottomNavigation.tvApts) R.color.nav_selected else R.color.white
            )
        )
        mainView.bottomNavigation.tvReqs.setColorFilter(
            ContextCompat.getColor(
                this,
                if (imageView == mainView.bottomNavigation.tvReqs) R.color.nav_selected else R.color.white
            )
        )
        mainView.bottomNavigation.tvNotifs.setColorFilter(
            ContextCompat.getColor(
                this,
                if (imageView == mainView.bottomNavigation.tvNotifs) R.color.nav_selected else R.color.white
            )
        )
        mainView.bottomNavigation.tvProfile.setColorFilter(
            ContextCompat.getColor(
                this,
                if (imageView == mainView.bottomNavigation.tvProfile) R.color.nav_selected else R.color.white
            )
        )
    }

    private fun checkAddress() {
        uservm.userProfileRes.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { userProfileResponse ->
                        val user = userProfileResponse.user
                        if (user.address.isNullOrEmpty()) {
                            mainView.bottomNavigation.ivErrorAddress.visibility = View.VISIBLE
                        } else {
                            mainView.bottomNavigation.ivErrorAddress.visibility = View.GONE
                        }

                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        //showDialog(message)
                    }
                }
                is Resource.Loading -> {
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

    override fun onBackPressed() {
        super.onBackPressed()
    }
}