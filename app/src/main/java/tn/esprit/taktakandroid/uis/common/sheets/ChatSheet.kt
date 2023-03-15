package tn.esprit.miniprojetinterfaces.Sheets

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.adapters.ChatAdapter
import tn.esprit.taktakandroid.databinding.FragmentChatSheetBinding
import tn.esprit.taktakandroid.models.ChatMessage


class ChatSheet : BottomSheetDialogFragment() {
    private lateinit var mainView: FragmentChatSheetBinding
    private val chatAdapter = ChatAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (dialog as? BottomSheetDialog)?.behavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as? BottomSheetDialog)?.behavior!!.skipCollapsed=true
        mainView = FragmentChatSheetBinding.inflate(layoutInflater, container, false)

        scrollToBottomWhenTyping()
        isKeyboardDisplayed()
        setupRecycler()
        return mainView.root
    }

    private fun scrollToBottomWhenTyping(){
        mainView.etMsgContent.doOnTextChanged { _, _, _, _ ->
            mainView.rvChat.scrollToPosition(
                getMessages().size - 1
            )
        }
    }
    private fun isKeyboardDisplayed(){
        mainView.root.viewTreeObserver
            .addOnGlobalLayoutListener {

                val r = Rect();
                mainView.root.getWindowVisibleDisplayFrame(r);

                val heightDiff = mainView.root.rootView.height - r.height();
                if (heightDiff > 0.25 * mainView.root.rootView.height
                ) {
                    mainView.tlContent.hint=""
                    mainView.rvChat.scrollToPosition(
                        getMessages().size - 1
                    )
                }

            }
    }

    private fun setupRecycler() {
        mainView.rvChat.adapter = chatAdapter
        mainView.rvChat.layoutManager = LinearLayoutManager(this.context)
        chatAdapter.diff.submitList(getMessages())
        mainView.rvChat.scrollToPosition(getMessages().size - 1)
    }


    private fun getMessages(): MutableList<ChatMessage> {
        return mutableListOf(

            ChatMessage(
                "Hello", "11:22 PM", true
            ),
            ChatMessage(
                "Hello", "11:23 PM", false
            ),
            ChatMessage(
                "how are you?", "11:24 PM", true
            ),
            ChatMessage(
                "fine ?", "11:25 PM", true
            ),
            ChatMessage(
                "yes", "11:26 PM", false
            ),
            ChatMessage(
                "and you ?", "11:27 PM", false
            ),
            ChatMessage(
                "great ", "11:28 PM", true
            ),
            ChatMessage(
                "do you know how to add alpha/opacity to the style file? Like it should take the normal color with an opacity and once they start typing, it should take the activated or highlighted color ",
                "11:29 PM",
                false
            ),
            ChatMessage(
                "nice yes ", "11:32 PM", true
            ),
            ChatMessage(
                "hahahhahaha", "11:31 PM", false
            ),
            ChatMessage(
                "where are you", "11:40 PM", true
            ),
            ChatMessage(
                "????", "11:50 PM", true
            ),
            ChatMessage(
                "?", "11:54 PM", true
            ),
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Debug", "dissmised onDestroy")
    }


}


