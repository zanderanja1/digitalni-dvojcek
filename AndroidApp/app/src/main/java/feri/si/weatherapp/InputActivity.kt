package feri.si.weatherapp

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import feri.si.weatherapp.databinding.ActivityInputBinding
import okhttp3.OkHttpClient
import org.json.JSONObject

class InputActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityInputBinding
    private lateinit var launcherSelectImage: ActivityResultLauncher<Intent>
    private lateinit var launcherTakePhoto: ActivityResultLauncher<Intent>
    private var currentLocation: Location? = null
    private lateinit var locationManager: LocationManager
    lateinit var app: MyApplication
    private var image: Uri = Uri.EMPTY


    fun parseWeatherApiResponse(responseBody: String): String {
        val jsonObject = JSONObject(responseBody)

        // Parse location information
        val locationObject = jsonObject.getJSONObject("location")
        val locationName = locationObject.getString("name")
        val locationRegion = locationObject.getString("region")
        val locationCountry = locationObject.getString("country")
        val cloud = jsonObject.getJSONObject("current").getInt("cloud")

        // Parse condition information
        val conditionObject = jsonObject.getJSONObject("current").getJSONObject("condition")
        val conditionText = conditionObject.getString("text")
        val conditionIcon = conditionObject.getString("icon")

        // Create WeatherData object
        return "${locationName}, ${locationRegion}\n${locationCountry}"
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MyApplication
        viewBinding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        isLocationPermissionGranted()

        val applicationInfo: ApplicationInfo = application.packageManager.getApplicationInfo(application.packageName, PackageManager.GET_META_DATA)

        val apiKey = applicationInfo.metaData.getString("MAP_API")

        fun updateLocation() {
            var queue: RequestQueue = Volley.newRequestQueue(context)
            Log.v("lokacija gps", "longitude: ${location?.longitude}, latitude: ${location?.latitude}")

            Log.v("lokacija network", "network: $location")

            viewBinding.textView.text = "Latitude: ${location?.latitude}\nLongitutde: ${location?.longitude}"
            val latitude = location?.latitude
            val longitude = location?.longitude
            val API_CALL = "https://api.weatherapi.com/v1/current.json?key=$apiKey&q=$latitude $longitude&aqi=no"
            //cloud = Cloud cover as percentage

            val stringRequest = StringRequest(
                Request.Method.GET, API_CALL,
                { response ->
                    Log.v("response", response.toString())

                    viewBinding.textView2.text = "${parseWeatherApiResponse(response)}"
                },
                {  response -> viewBinding.textView2.text = "That didn't work! "
                    Log.v("response","${response.toString()}")})

            queue.add(stringRequest);
        }


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

        val pickVisualMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != Uri.EMPTY) {
                image = uri!!
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                viewBinding.imageView.setImageURI(image)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        launcherTakePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                image = result.data!!.getStringExtra("uri")!!.toUri()
                viewBinding.imageView.setImageURI(image)
            }
        }

        viewBinding.imageCaptureButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            launcherTakePhoto.launch(intent)
        }

        viewBinding.imageSelectButton.setOnClickListener {
            pickVisualMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        viewBinding.addButton.setOnClickListener {
            try {
                val data = Intent()
                Log.d("InputSend", "${location?.latitude} ${location?.longitude} ${image}")
                data.putExtra("latitude", location?.latitude.toString())
                data.putExtra("longitude", location?.longitude.toString())
                data.putExtra("imageUri", image.toString())
                setResult(RESULT_OK, data)
                finish()

            } catch (e: Exception) {
                Toast.makeText(this,e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}