package feri.si.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.net.URI
import feri.si.weatherapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var launcherSelectImage: ActivityResultLauncher<Intent>
    private lateinit var launcherTakePhoto: ActivityResultLauncher<Intent>
    private var uri: URI? = null
    private var uriString: String? = ""
    private var currentLocation: Location? = null
    private lateinit var locationManager: LocationManager

    val API_KEY = Constants.API


    data class WeatherData(
        val locationName: String,
        val cloud: Int,
        val conditionText: String,
        val conditionIcon: String
    )

    fun parseWeatherApiResponse(responseBody: String): String {
        val jsonObject = JSONObject(responseBody)

        // Parse location information
        val locationObject = jsonObject.getJSONObject("location")
        val locationName = locationObject.getString("name")
        val cloud = jsonObject.getJSONObject("current").getInt("cloud")

        // Parse condition information
        val conditionObject = jsonObject.getJSONObject("current").getJSONObject("condition")
        val conditionText = conditionObject.getString("text")
        val conditionIcon = conditionObject.getString("icon")

        // Create WeatherData object
       return "${locationName},\n $cloud, $conditionText "
    }
    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                156
            )
            false
        } else {
            true
        }
    }

    var location: Location? = null


    private var context: Context =  this

    fun updateLocation() {
        var queue: RequestQueue = Volley.newRequestQueue(context)
        Log.v("lokacija gps", "longitude: ${location?.longitude}, latitude: ${location?.latitude}")

        Log.v("lokacija network", "network: $location")

        viewBinding.textView.text = "latitude: ${location?.latitude}, longitutde: ${location?.longitude}"
        val latitude = location?.latitude
        val longitude = location?.longitude
        val API_CALL = "https://api.weatherapi.com/v1/current.json?key=$API_KEY&q=$latitude $longitude&aqi=no"
        //cloud = Cloud cover as percentage

        val stringRequest = StringRequest(Request.Method.GET, API_CALL,
            { response ->
                Log.v("response", response.toString())
                // Display the first 500 characters of the response string.
                viewBinding.textView2.text = "${parseWeatherApiResponse(response)}"
            },
            {  response -> viewBinding.textView2.text = "That didn't work! "
            Log.v("response","${response.toString()}")})

        queue.add(stringRequest);
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        isLocationPermissionGranted()

        //locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            156
        )
        var locationListener: LocationListener? = null

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
             locationListener = object : LocationListener {
                override fun onLocationChanged(locationtemp: Location) {

                    // Called when a new location is found by the network location provider.
                    //Toast.makeText(baseContext, "location is:$locationtemp", Toast.LENGTH_LONG).show()
                    location = locationtemp
                    updateLocation()
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}

            }

        }

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            15000,
            0f,
            locationListener!!
        )



        launcherTakePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                uriString = result.data!!.getStringExtra("uri")
                uriString?.let { Log.v("uri 2", it) }
                viewBinding.imageView.setImageURI(Uri.parse(uriString))
            }
        }

        viewBinding.imageCaptureButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            launcherTakePhoto.launch(intent)
        }

        launcherSelectImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                uriString = result.data!!.getStringExtra("uri")
                uriString?.let { Log.v("uri 2", it) }
                viewBinding.imageView.setImageURI(Uri.parse(uriString))
            }
        }

        viewBinding.imageSelectButton.setOnClickListener {
            val intent = Intent(this, ImageSelectActivity::class.java)
            launcherSelectImage.launch(intent)
        }

    }


}