package tn.esprit.taktakandroid.uis.common.sheets.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatViewModelProviderFactory (
    private val aptId:String,
    private val currentUserId:String
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(aptId,currentUserId) as T
    }
}