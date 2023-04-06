package tn.esprit.taktakandroid.uis.common.mapView

import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ActivityMapBinding
import java.io.IOException
import java.util.*

const val TAG = "MapActivity"

class MapActivity : AppCompatActivity() {
    lateinit var mainView: ActivityMapBinding
    lateinit var viewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        mainView = ActivityMapBinding.inflate(layoutInflater)
        setContentView(mainView.root)
        val factory=MapViewModelProviderFactory(application)
        viewModel=ViewModelProvider(this,factory)[MapViewModel::class.java]

        initResult()
        mapSetup()
        addMarker()

        mainView.fabSuccess.setOnClickListener {
            viewModel.getAddress()

        }

        mainView.fab.setOnClickListener {
            if (mainView.tlGuide.visibility==View.VISIBLE) {
                viewModel.isGuideOn(false)
            } else {
                viewModel.isGuideOn(true)
            }
        }

        viewModel.guideOn.observe(this){isVisible->
            if(isVisible){
                mainView.tlGuide.visibility=View.VISIBLE
                //TODO add guide phrases
            }
            else{
                mainView.tlGuide.visibility=View.GONE
            }
        }
        viewModel.markerOn.observe(this){isShown->
            if(isShown){
                mainView.fabSuccess.visibility=View.VISIBLE
            }
            else{
                mainView.fabSuccess.visibility=View.GONE
            }

        }

        viewModel.finalAddress.observe(this){address->
            if(!address.isNullOrEmpty()){
                intent.putExtra("location", address)
                setResult(RESULT_OK, intent)
                finish()
            }

        }
    }
    private fun mapSetup(){
        //map source
        mainView.map.setTileSource(TileSourceFactory.MAPNIK)

        //map config
        mainView.map.setBuiltInZoomControls(false)
        mainView.map.setMultiTouchControls(true)

        //center map and zoom
        val mapController = mainView.map.controller
        mapController.setCenter(GeoPoint(36.806389,10.181667));
        mapController.setZoom(9.5)

        // current location
        val mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mainView.map)
        mMyLocationOverlay.disableMyLocation()
        mMyLocationOverlay.disableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            runOnUiThread {
                mapController.setCenter(mMyLocationOverlay.myLocation)
                mapController.animateTo(mMyLocationOverlay.myLocation)
            }
        }
        mainView.map.overlays.add(mMyLocationOverlay)

    }
    private fun addMarker(){
        val marker = Marker(mainView.map)
        marker.setInfoWindow(null)
        marker.icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_loca)
        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {

                return false
            }
            override fun longPressHelper(p: GeoPoint?): Boolean {
                marker.position = GeoPoint(p)
                viewModel.setLocation(p!!)
                viewModel.isMarkerOn(true)
                mainView.map.overlays.add(marker)
                mainView.map.invalidate()

                marker.setOnMarkerClickListener { _, _ ->
                        marker.closeInfoWindow()
                        viewModel.setLocation(p)
                        viewModel.isMarkerOn(false)
                        mainView.map.overlays.remove(marker)
                        mainView.map.invalidate()

                    true
                }
                return false
            }
        }

        mainView.map.overlays.add(MapEventsOverlay(mReceive))
    }
    private fun initResult(){
        intent.putExtra("location", "")
        setResult(RESULT_OK, intent)
    }


    override fun onResume() {
        super.onResume()
        mainView.map.onResume()
    }
    override fun onPause() {
        super.onPause()
        mainView.map.onPause()
    }
}
