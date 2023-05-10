package tn.esprit.taktakandroid.utils



object ChannelConfig {
    private var channelID=0
    private var channelName=0
        fun generateChannelID():String{
            channelID++
            return "Channel_ID_$channelID"
        }

    fun generateChannelName():String{
        channelName++
        return "Channel_Name_$channelName"
    }

}