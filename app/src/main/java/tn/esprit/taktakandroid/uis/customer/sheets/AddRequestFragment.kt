package tn.esprit.taktakandroid.uis.customer.sheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import tn.esprit.taktakandroid.R

class AddRequestFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_add_request, container, false)
        setupTosSpinner(view)
        return view
    }

    private fun setupTosSpinner(view:View) {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item_tos,
            listOf("Installation", "Repair","Maintenance")
        )
        adapter.setDropDownViewResource(R.layout.spinner_custom_dropdown)
        view.findViewById<Spinner>(R.id.spService).adapter = adapter
    }
}