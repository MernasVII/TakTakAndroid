package tn.esprit.taktakandroid.uis.common.sheets.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import tn.esprit.taktakandroid.models.entities.ChatMessage
import java.util.*

class ChatViewModel(aptId: String,currentUserId:String) : ViewModel() {

    private var messagesCollection: CollectionReference
    private lateinit var messagesRegistration: ListenerRegistration
    val messages: MutableLiveData<MutableList<ChatMessage>> = MutableLiveData()
    private var thisUserId:String

    init {
        thisUserId=currentUserId
        messagesCollection = FirebaseFirestore.getInstance().collection(aptId!!)
        getMessages()
    }

    fun getMessagesLiveData(): LiveData<MutableList<ChatMessage>> {
        return messages
    }

    fun sendMessage(messageText: String) {
        if (messageText.isNotEmpty()) {
            val message = hashMapOf(
                "text" to messageText,
                "timestamp" to Timestamp(Date()),
                "user" to thisUserId
            )
            messagesCollection.add(message)
                .addOnSuccessListener { documentReference ->
                    Log.d("TESTINGG", "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("TESTINGG", "Error adding document", e)
                }
        }
    }

    private fun getMessages() {
        messagesRegistration =
            messagesCollection.orderBy("timestamp").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle errors
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = mutableListOf<ChatMessage>()
                    snapshot.documents.forEach { document ->
                        val messageId = document.id
                        val timestamp = document.getDate("timestamp")
                        val userId = document.getString("user")
                        val text = document.getString("text")
                        var isSent = false
                        if (userId == thisUserId) {
                            isSent = true
                        }
                        list.add(ChatMessage(text!!, timestamp.toString(), isSent, messageId))
                    }
                    messages.postValue(list)
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        messagesRegistration.remove()
    }

}
