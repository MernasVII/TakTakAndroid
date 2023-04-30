package tn.esprit.taktakandroid.models.entities

data class ChatMessage(

    val content: String,
    val time: String,
    val sent:Boolean,
    var id: String?=null,
)
