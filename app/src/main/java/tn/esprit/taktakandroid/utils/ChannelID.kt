package tn.esprit.taktakandroid.utils



object NotifIDGenerator {
    private var notifID=0
        fun generateNotifID(): Int{
            notifID++
            return notifID
        }



}