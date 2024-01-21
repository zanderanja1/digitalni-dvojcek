package feri.si.weatherapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import feri.si.weatherapp.databinding.ActivityMainBinding
import feri.si.weatherapp.databinding.ActivityMapBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration

import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private var context: Context =  this
    lateinit var app: MyApplication
    lateinit var map: MapView
    var startPoint: GeoPoint = GeoPoint(46.55951, 15.63970);
    lateinit var mapController: IMapController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MyApplication
        Configuration.getInstance()
            .load(applicationContext, this.getPreferences(Context.MODE_PRIVATE))

        binding = ActivityMapBinding.inflate(layoutInflater)
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        mapController = map.controller
        mapController.setCenter(startPoint)
        mapController.setZoom(10.5)
        setContentView(binding.root)

        map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                AlertDialog.Builder(context)
                    .setTitle("Confirm")
                    .setMessage("Do you want to pick this spot?")
                    .setPositiveButton("Yes") { _, _ ->
                        Log.d("PickLocation","${p.latitude}, ${p.longitude}")
                        // The user said yes
                        // Return the latitude and longitude to the parent activity
                        val data = Intent()
                        data.putExtra("latitude", p.latitude)
                        data.putExtra("longitude", p.longitude)
                        setResult(RESULT_OK, data)
                        finish()
                    }
                    .setNegativeButton("No", null)
                    .show()
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }))
    }


}
