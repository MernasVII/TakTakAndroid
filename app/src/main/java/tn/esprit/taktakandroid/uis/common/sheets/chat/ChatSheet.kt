package tn.esprit.taktakandroid.uis.common.sheets.chat

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.adapters.ChatAdapter
import tn.esprit.taktakandroid.databinding.SheetFragmentChatBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.uis.common.aptspending.PendingAptsViewModel
import tn.esprit.taktakandroid.uis.common.aptspending.PendingAptsViewModelFactory
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants


class ChatSheet : BottomSheetDialogFragment() {
    private lateinit var mainView: SheetFragmentChatBinding
    private lateinit var viewModel: ChatViewModel
    private val chatAdapter = ChatAdapter()

    private var aptId: String? = ""
    private lateinit var user: User
    private var currentUserId: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentChatBinding.inflate(layoutInflater, container, false)
        aptId = arguments?.getString("aptId")
        user = arguments?.getParcelable("user")!!
        lifecycleScope.launch {
            currentUserId = AppDataStore.readString(Constants.USER_ID)
        }
        viewModel = ViewModelProvider(
            this,
            ChatViewModelProviderFactory(aptId!!,currentUserId!!)
        )[ChatViewModel::class.java]
        setHeader()

        dialogBehavior()
        scrollToBottomWhenTyping()
        isKeyboardDisplayed()
        setupRecycler()

        lifecycleScope.launch {
            currentUserId = AppDataStore.readString(Constants.USER_ID)
        }

        mainView.ivSend.setOnClickListener {
            val messageText = mainView.etMsgContent.text.toString().trim()
            viewModel.sendMessage(messageText)
            mainView.etMsgContent.setText("")
        }

        return mainView.root
    }

    private fun setHeader() {
        Glide.with(this).load(Constants.IMG_URL + user.pic)
            .into(mainView.ivPic)
        mainView.tvFullname.text=user.firstname+" "+user.lastname
    }

    private fun dialogBehavior() {
        (dialog as? BottomSheetDialog)?.behavior!!.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
    }

    private fun scrollToBottomWhenTyping() {
        mainView.etMsgContent.doOnTextChanged { _, _, _, _ ->
            mainView.rvChat.scrollToPosition(
                viewModel.messages.value?.size?.minus(1) ?: 0
            )
        }
    }

    private fun isKeyboardDisplayed() {
        mainView.root.viewTreeObserver
            .addOnGlobalLayoutListener {

                val r = Rect();
                mainView.root.getWindowVisibleDisplayFrame(r);

                val heightDiff = mainView.root.rootView.height - r.height();
                if (heightDiff > 0.25 * mainView.root.rootView.height
                ) {
                    mainView.tlContent.hint = ""
                    mainView.rvChat.scrollToPosition(
                        viewModel.messages.value?.size?.minus(1) ?: 0
                    )
                }

            }
    }

    private fun setupRecycler() {
        mainView.rvChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this.context)
        }

        viewModel.messages.observe(viewLifecycleOwner, Observer { messages ->
            messages?.let {
                chatAdapter.submitList(messages)
                mainView.rvChat.scrollToPosition(messages.size - 1)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Debug", "Dismissed onDestroy")
    }
}


