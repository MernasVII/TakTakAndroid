package tn.esprit.taktakandroid.uis.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.customer.CustomerReqsFragment
import tn.esprit.taktakandroid.uis.HomeViewModel
import tn.esprit.taktakandroid.uis.sp.SPReqsFragment
import androidx.activity.viewModels
import tn.esprit.taktakandroid.uis.HomeViewModelProviderFactory
import tn.esprit.taktakandroid.uis.common.userprofile.UserProfileFragment
import tn.esprit.taktakandroid.uis.customer.SPsFragment

class HomeActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_home)
        val initialFragment = SPsFragment()
        replaceFragment(initialFragment)

        findViewById<TextView>(R.id.tv_providers).setOnClickListener {
            replaceFragment(spsFragment)
        }

        findViewById<TextView>(R.id.tv_apts).setOnClickListener {
            replaceFragment(aptsFragment)
        }

        //TODO condition if sp=>sp reqs else =>customer reqs
        /*if(isSP){
        replaceFragment(spReqsFragment)
        }else{*/
        findViewById<TextView>(R.id.tv_reqs).setOnClickListener {
            replaceFragment(spReqsFragment)
        }
        //}
        findViewById<TextView>(R.id.tv_notifs).setOnClickListener {
            replaceFragment(notifsFragment)
        }

        findViewById<TextView>(R.id.tv_profile).setOnClickListener {
            replaceFragment(profileFragment)
        }

    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

