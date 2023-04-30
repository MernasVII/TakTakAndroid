package tn.esprit.taktakandroid.uis.common.aptspending

interface AptItemTouchHelperListener {
    fun onAptSwipedLeft(aptId: String,customerID:String)
    fun onAptSwipedRight(aptId: String,customerID:String)
}
