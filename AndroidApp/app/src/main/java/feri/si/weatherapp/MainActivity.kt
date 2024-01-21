package feri.si.weatherapp
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import feri.si.weatherapp.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    lateinit var app: MyApplication
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MyApplication
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

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
            fetchClouds()
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


}