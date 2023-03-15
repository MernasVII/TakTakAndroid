package tn.esprit.taktakandroid.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class OtpTextWatcher(
    private val currentIndex: Int,
    private val editTexts: List<EditText>
) :
    TextWatcher {
    private var isFirst = false
    private var isLast = false
    private var newTypedString = ""


    init {
        if (currentIndex == 0) isFirst =
            true else if (currentIndex == editTexts.size - 1) isLast = true

    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }

    }

    override fun afterTextChanged(s: Editable) {
        val text = newTypedString

        if (text.length == 4) {
            editTexts.forEach { it.removeTextChangedListener(this) }
            for (i in editTexts.indices) {
                val digit = text[i].toString()
                editTexts[i].setText(digit)

            }
            editTexts.forEach { it.addTextChangedListener(this) }
        }

        if (text.length == 1) moveToNext() else if (text.isEmpty()) moveToPrevious()
    }

    private fun moveToNext() {
        if (!isLast) editTexts[currentIndex + 1].requestFocus()
        if (isAllEditTextsFilled && isLast) { // isLast is optional
            editTexts[currentIndex].clearFocus()
        }
    }


    private fun moveToPrevious() {
        if (!isFirst) {
            editTexts[currentIndex - 1].setSelection(editTexts[currentIndex - 1].length())
            editTexts[currentIndex - 1].requestFocus()
        }
    }

    private val isAllEditTextsFilled: Boolean
        get() {
            for (editText in editTexts) if (editText.text.toString()
                    .trim { it <= ' ' }.isEmpty()
            ) return false

            return true
        }


}