package tn.esprit.taktakandroid.uis

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.databinding.LayoutDialogBinding

open class SheetBaseFragment : BottomSheetDialogFragment() {

    fun progressBarVisibility(visible: Boolean, view: View) {
        if (visible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
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
}