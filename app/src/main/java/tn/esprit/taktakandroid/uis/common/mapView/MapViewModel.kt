package tn.esprit.taktakandroid.uis.common.mapView

import android.app.Application
import android.location.Geocoder
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.util.*


class MapViewModel(private val application: Application) : AndroidViewModel(
    application
) {
    private val _finalAddress = MutableLiveData<String>()
    val finalAddress: LiveData<String>
        get() = _finalAddress

    private val _location = MutableLiveData<GeoPoint>()
    val location: LiveData<GeoPoint>
        get() = _location


    private val _markerOn = MutableLiveData<Boolean>()
    val markerOn: LiveData<Boolean>
        get() = _markerOn

    private val _guideOn = MutableLiveData<Boolean>()
    val guideOn: LiveData<Boolean>
        get() = _guideOn



    fun setLocation(location: GeoPoint) {
        _location.value = location
    }

    fun isMarkerOn(markerOn: Boolean) {
        _markerOn.value = markerOn
    }
    fun isGuideOn(guideOn: Boolean) {
        _guideOn.value = guideOn
    }

   init {
       _location.value=GeoPoint(0.0,0.0)
       _markerOn.value=false
       _guideOn.value=false
   }

    fun getAddress(){
        val geocoder = Geocoder(application.applicationContext, Locale.getDefault())
        var stringBuilder = ""

        try {
            viewModelScope.launch(Dispatchers.IO) {
                val addresses = geocoder.getFromLocation(_location.value!!.latitude, _location.value!!.longitude, 1)
                if (Geocoder.isPresent()) {
                    if (addresses!!.size > 0) {
                        val returnAddress = addresses[0]
                        val localityString = returnAddress.locality
                        val state = returnAddress.adminArea
                        stringBuilder=
                            "$state $localityString"
                        _finalAddress.postValue(stringBuilder.trim())
                    }
                }}
        } catch (e: IOException) {
            Toast.makeText(application, e.message, Toast.LENGTH_LONG).show()

        }
    }




}

