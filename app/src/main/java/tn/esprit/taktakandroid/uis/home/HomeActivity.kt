package tn.esprit.taktakandroid.uis.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.Fragment
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.customer.CustomerReqsFragment
import tn.esprit.taktakandroid.uis.sp.SPReqsFragment
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.databinding.ActivityHomeBinding
import tn.esprit.taktakandroid.uis.common.AptsFragment
import tn.esprit.taktakandroid.uis.common.NotifsFragment
import tn.esprit.taktakandroid.uis.common.userprofile.UserProfileFragment
import tn.esprit.taktakandroid.uis.customer.spslist.SPsFragment
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants

class HomeActivity : AppCompatActivity() {
    private lateinit var mainView : ActivityHomeBinding
    private val TAG="HomeActivity"

    private val spsFragment = SPsFragment()
    private val aptsFragment = AptsFragment()
    private val customerReqsFragment = CustomerReqsFragment()
    private val spReqsFragment = SPReqsFragment()
    private val notifsFragment = NotifsFragment()
    private val profileFragment = UserProfileFragment()

    val viewModel: HomeViewModel by viewModels {
        HomeViewModelProviderFactory(UserRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mainView.root)

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

        findViewById<TextView>(R.id.tv_profile).setOnClickListener {
            replaceFragment(profileFragment)
        }

    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
