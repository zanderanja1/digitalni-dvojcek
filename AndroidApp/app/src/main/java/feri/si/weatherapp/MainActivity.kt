package feri.si.weatherapp
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import feri.si.weatherapp.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

import java.io.IOException
import java.lang.Exception

import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale

class DataAdapter(private val data: Array<out String>) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.DataTextView)

        fun bind(item: String) {
            textView.text = item
        }
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    lateinit var app: MyApplication
    val client = OkHttpClient()
    lateinit var service:WeatherNotification


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MyApplication
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        WeatherNotification(applicationContext)



        fetchCloudsAsync { clouds ->
            runOnUiThread {
                val recyclerView: RecyclerView = viewBinding.recycleView
                Log.v("clouds", clouds)
                //val dataFromActivity: List<String> = listOf(clouds, "a", "b", "c", "d", "e", "f")
                var dataFromActivity = parseResponse(clouds)
                val adapter = DataAdapter(dataFromActivity.orEmpty())
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)
            }
        }




        val getCloud = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                try {
                    val data: Intent? = result.data
                    val latitude = data?.getStringExtra("latitude")
                    val longitude = data?.getStringExtra("longitude")
                    val imageUriString = data?.getStringExtra("imageUri")
                    val imageUri = Uri.parse(imageUriString)
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bytes = inputStream?.readBytes()

                    val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("latitude", latitude)
                        .addFormDataPart("longitude", longitude)
                        .addFormDataPart("postedBy", app.getID())
                        .addFormDataPart("image", "file", RequestBody.create(MediaType.parse("image/*"), bytes))
                        .build()

                    /*val buffer = okio.Buffer()
                    requestBody.writeTo(buffer)
                    val requestBodyString = buffer.readUtf8()
                    Log.d("InputSend",requestBodyString)*/

                    val request = Request.Builder()
                        .url("http://192.168.0.27:3001/clouds")
                        .post(requestBody)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (!response.isSuccessful) {
                                throw IOException("Unexpected code $response")
                            }
                            else{
                                Log.v("cloud", response.toString())
                                Log.v("cloud body", response.body().toString())
                                val ratioString = parseRatioFromResponse(response.toString())
                                service.showNotification(ratioString)
                            }
                        }
                    })
                } catch (e: Exception) {
                    Toast.makeText(this,e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }


        viewBinding.inputButton.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            getCloud.launch(intent)
        }

        viewBinding.simulateButton.setOnClickListener {
            val intent = Intent(this, SimulateActivity::class.java)
            getCloud.launch(intent)
        }
    }

    fun fetchClouds() {
        val request = Request.Builder()
            .url("http://192.168.0.27:3001/clouds")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                } else {

                    Log.d("Response", response.body().toString())
                }
            }
        })

    }
    fun fetchCloudsAsync(callback: (String) -> Unit) {
        val request = Request.Builder()
            .url("http://192.168.0.27:3001/clouds")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val cloudResponse = "napaka: ${e.printStackTrace()}"
                callback(cloudResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    val cloudResponse = "napaka: $response"
                    callback(cloudResponse)
                } else {
                    val cloudResponse = response.body()?.string() ?: ""
                    Log.v("clouds: response", cloudResponse)
                    Log.d("Response", response.body().toString())
                    callback(cloudResponse)
                }
            }
        })
    }
    fun parseResponse(response: String): Array<String> {
        val jsonArray = JSONArray(response)
        val locationArray = mutableListOf<String>()
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val locationObject = jsonObject.getJSONObject("location")
            val ratio = jsonObject.getDouble("ratio").toString()
            val weather = jsonObject.getString("weather").toString()
            val dateString = jsonObject.getString("date").toString()
            val date = inputFormat.parse(dateString)

            val latitude = locationObject.getString("latitude")
            val longitude = locationObject.getString("longitude")
            var address:Address? = null
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            var addresses = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    address = addresses?.get(0)
                }
            }


            val formattedDate = outputFormat.format(date)
            val cloudRatio = ratio.subSequence(0, 5).toString().toFloat()
            var icon = ""
            if (cloudRatio >= 0.8)
                icon = "☁️"
            else if (cloudRatio >= 0.6)
                icon = "⛅️️"
            else if (cloudRatio >= 0.4)
                icon = "🌤️"
            else
                icon = "️☀️"
            val outputString =
                "$icon️  Cloudiness ratio: ${cloudRatio * 100}%,  ${weather},  ${formattedDate}\n${address?.adminArea}, ${address?.thoroughfare}, ${address?.countryName}"
            locationArray.add(outputString)

        }

        return locationArray.toTypedArray()
    }
    fun parseRatioFromResponse(response: String): String {
        val jsonArray = JSONArray(response)
        var outputString = "0.0"
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val ratio = jsonObject.getDouble("ratio").toString()

            outputString = ratio.subSequence(0,5).toString()
        }

        return outputString
    }


}

