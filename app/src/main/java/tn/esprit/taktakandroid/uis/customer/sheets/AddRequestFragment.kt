package tn.esprit.taktakandroid.uis.customer.sheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentAddRequestBinding

class AddRequestFragment : Fragment() {

    private lateinit var mainView: FragmentAddRequestBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentAddRequestBinding.inflate(layoutInflater, container, false)
        setupTosSpinner()
        return mainView.root
    }

    private fun setupTosSpinner() {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item_tos,
            listOf("Installation", "Repair", "Maintenance")
        )
        adapter.setDropDownViewResource(R.layout.spinner_custom_dropdown)
        mainView.spService.adapter = adapter
    }
}