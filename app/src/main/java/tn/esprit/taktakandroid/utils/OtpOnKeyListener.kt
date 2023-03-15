package tn.esprit.taktakandroid.utils

import android.view.KeyEvent
import android.view.View
import android.widget.EditText

class OtpOnKeyListener internal constructor(
    private val currentIndex: Int,
    private val editTexts: List<EditText>
) : View.OnKeyListener {
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
            if (editTexts[currentIndex].text.toString()
                    .isEmpty() && currentIndex != 0
            ) editTexts[currentIndex - 1].requestFocus()
        }
        return false
    }
}