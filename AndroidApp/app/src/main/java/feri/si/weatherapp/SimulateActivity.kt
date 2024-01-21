package feri.si.weatherapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import feri.si.weatherapp.databinding.ActivitySimulateBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody

import okhttp3.OkHttpClient
import org.json.JSONObject
import java.io.IOException
import kotlin.Exception
import kotlin.random.Random



class SimulateActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimulateBinding
    lateinit var app: MyApplication
    var client = OkHttpClient()
    var interval = 10
    var rangeMin = 0f
    var rangeMax = 1f
    var latitude = 46.056946
    var longitude = 14.505751


    fun parseWeatherApiResponse(responseBody: String): String {
        val jsonObject = JSONObject(responseBody)

        // Parse location information
        val locationObject = jsonObject.getJSONObject("location")
        val locationName = locationObject.getString("name")
        val locationRegion = locationObject.getString("region")
        val locationCountry = locationObject.getString("country")

        // Create WeatherData object
        return "${locationName}, ${locationRegion}, ${locationCountry}"
    }

    private var context: Context =  this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MyApplication
        binding = ActivitySimulateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intervalValueView = binding.intervalValue
        val editIntervalView = binding.editInterval
        val minRangeView = binding.minValue
        val maxRangeView = binding.maxValue
        val locationValue = binding.locationValue
        val enableSwitch = binding.enableSwitch
        val handler = Handler(Looper.getMainLooper())
        val runnableCode: Runnable = object : Runnable {
            override fun run() {

                    val requestBody = FormBody.Builder()
                        .add("postedBy", app.getID().toString())
                        .add("ratio", "${Random.nextDouble(minRangeView.text.toString().toDouble(), maxRangeView.text.toString().toDouble())}")
                        .add("latitude", latitude.toString())
                        .add("longitude", longitude.toString())
                        .build()

                    val request = okhttp3.Request.Builder()
                        .url("http://192.168.0.27:3001/clouds/simulated")
                        .post(requestBody)
                        .build()
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val response = client.newCall(request).execute()
                            withContext(Dispatchers.Main) {
                                Log.d("Handlers", "Sent simulated data")
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Log.d("Handlers", "Sent simulated data failed: ${e.message}")
                            }
                        }
                    }



                handler.postDelayed(this, interval.toLong() * 60000)
                //handler.postDelayed(this, 6000)

            }
        }

        val applicationInfo: ApplicationInfo = application.packageManager.getApplicationInfo(application.packageName, PackageManager.GET_META_DATA)

        val apiKey = applicationInfo.metaData.getString("MAP_API")

        fun updateLocation() {
            var queue: RequestQueue = Volley.newRequestQueue(context)
            val API_CALL = "https://api.weatherapi.com/v1/current.json?key=$apiKey&q=$latitude $longitude&aqi=no"

            val stringRequest = StringRequest(
                Request.Method.GET, API_CALL,
                { response ->
                    Log.v("response", response.toString())
                    // Display the first 500 characters of the response string.
                    locationValue.text = "${parseWeatherApiResponse(response)}"
                },
                {  response -> locationValue.text = "That didn't work! "
                    Log.v("response","${response.toString()}")})

            queue.add(stringRequest);
        }

        updateLocation()


        intervalValueView.text = "Repeat every ${interval} minutes"


        editIntervalView.visibility = View.GONE

        minRangeView.setText(rangeMin.toString())
        maxRangeView.setText(rangeMax.toString())

        intervalValueView.setOnClickListener {
            intervalValueView.visibility = View.GONE
            editIntervalView.setText(interval.toString())
            editIntervalView.visibility = View.VISIBLE
            editIntervalView.requestFocus()
        }

        val getLocation = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                try {
                    val data: Intent? = result.data
                    latitude = data?.getDoubleExtra("latitude", latitude)!!
                    longitude = data?.getDoubleExtra("longitude", longitude)!!
                    updateLocation()
                } catch (e: Exception) {
                    Toast.makeText(this,e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                try {
                    val input = s.toString().toFloat()
                    if (input < 0.0 || input > 1.0) {
                        s.clear()
                    }
                } catch (e: NumberFormatException) {
                    // Handle exception
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        }

        minRangeView.addTextChangedListener(textWatcher)
        maxRangeView.addTextChangedListener(textWatcher)

        editIntervalView.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                editIntervalView.visibility = View.GONE
                interval = editIntervalView.text.toString().toInt()
                intervalValueView.text = "Repeat every ${interval} minutes"
                intervalValueView.visibility = View.VISIBLE
                true
            } else {
                false
            }
        }

        binding.locationValue.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            getLocation.launch(intent)
        }

        enableSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
                enableSwitch.text = "Enabled"
                // Start the runnable task
                handler.post(runnableCode)
            } else {
                // The switch is disabled/unchecked
                enableSwitch.text = "Disabled"
                // Remove the runnable task
                handler.removeCallbacks(runnableCode)
            }
        }

    }
}