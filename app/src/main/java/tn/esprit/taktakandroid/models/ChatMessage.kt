package tn.esprit.taktakandroid.models

data class ChatMessage(

    val content: String,
    val time: String,
    val sent:Boolean,
    var id: Long = 0,
)
