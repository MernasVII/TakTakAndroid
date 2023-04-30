package tn.esprit.miniprojetinterfaces.Sheets

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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.adapters.ChatAdapter
import tn.esprit.taktakandroid.databinding.SheetFragmentChatBinding
import tn.esprit.taktakandroid.models.entities.ChatMessage
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import java.util.*


class ChatSheet : BottomSheetDialogFragment() {
    private lateinit var mainView: SheetFragmentChatBinding
    private val chatAdapter = ChatAdapter()

    private lateinit var messagesCollection:CollectionReference
    private lateinit var messagesRegistration: ListenerRegistration
    private val messages: MutableList<ChatMessage> = mutableListOf()
    private var aptId: String? = ""
    private lateinit var user: User
    private var currentUserId: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentChatBinding.inflate(layoutInflater, container, false)
        aptId = arguments?.getString("aptId")
        user = arguments?.getParcelable<User>("user")!!
        messagesCollection = FirebaseFirestore.getInstance().collection(aptId!!)

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
            if (messageText.isNotEmpty()) {
                val message = hashMapOf(
                    "text" to messageText,
                    "timestamp" to Timestamp(Date()),
                    "user" to currentUserId
                )
                // Add the message to the list
                val sentMessage = ChatMessage(messageText, Timestamp(Date()).toString(), true, null)
                messages.add(sentMessage)
                chatAdapter.notifyItemInserted(messages.size - 1)
                messagesCollection.add(message)
                    .addOnSuccessListener { documentReference ->
                        Log.d("TESTINGG", "DocumentSnapshot written with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TESTINGG", "Error adding document", e)
                        messages.remove(sentMessage)
                        chatAdapter.notifyItemRemoved(messages.size)
                    }

                mainView.etMsgContent.setText("")
            }
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
                messages.size - 1
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
                        messages.size - 1
                    )
                }

            }
    }

    private fun setupRecycler() {
        mainView.rvChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this.context)
        }

        chatAdapter.diff.submitList(getMessages())
        mainView.rvChat.scrollToPosition(messages.size - 1)
    }


    fun getMessages(): MutableList<ChatMessage> {
        val messagesCollection = FirebaseFirestore.getInstance().collection(aptId!!)

        messagesRegistration=messagesCollection.orderBy("timestamp").addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle errors
                return@addSnapshotListener
            }

            if (snapshot != null) {
                messages.clear()
                snapshot.documents.forEach { document ->
                    val messageId = document.id
                    val timestamp = document.getDate("timestamp")
                    val userId = document.getString("user")
                    val text = document.getString("text")
                    var currentUserId:String?=""
                    lifecycleScope.launch {
                        currentUserId = AppDataStore.readString(Constants.USER_ID)
                        var isSent=false
                        if(userId.equals(currentUserId)){
                            isSent=true
                        }
                        messages.add(ChatMessage(text!!, timestamp.toString(), isSent, messageId))
                        chatAdapter.notifyDataSetChanged()
                        mainView.rvChat.scrollToPosition(messages.size - 1)
                    }
                }
            }
        }

        return messages
    }



    override fun onDestroy() {
        super.onDestroy()
        Log.d("Debug", "Dismissed onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messagesRegistration.remove()
    }


}


