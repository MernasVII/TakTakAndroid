package tn.esprit.taktakandroid.uis

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import tn.esprit.taktakandroid.databinding.LayoutDialogBinding
import tn.esprit.taktakandroid.databinding.LayoutDialogYesNoBinding

open class BaseFragment() :Fragment() {

    fun progressBarVisibility(visible: Boolean, view: View) {
        if (visible) {
            view.visibility = View.VISIBLE
            Log.d("CustomerBidsFragment", "progressBarVisibility: here1")
        } else {
            view.visibility = View.GONE
            Log.d("CustomerBidsFragment", "progressBarVisibility: here2")
        }
    }

    fun showDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        val binding = LayoutDialogBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.tvMessage.text = message
        binding.tvBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }
    fun showChoiceDialog(message:String,actionMethod: () -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        val binding = LayoutDialogYesNoBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.tvContent.text = message
        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        binding.btnYes.setOnClickListener {
            actionMethod()
            dialog.dismiss()
        }
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }
}