package feri.si.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import feri.si.weatherapp.databinding.ActivityLiveCameraBinding


class LiveCameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var binding = ActivityLiveCameraBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val webView = binding.webView
        webView.settings.javaScriptEnabled = true
        //
        val keys = arrayListOf<String>("SKgiuEUNJ9U","9xF2uTYMK50")
        val url = "https://www.youtube.com/embed/SKgiuEUNJ9U"

        val screenWidth = binding.webView.width
        Log.v("width: ", screenWidth.toString())
        val htmlCode = "<html><body><iframe id=\"player\" type=\"text/html\" " +
                "src=\"" + url + "\" " +
                "frameborder=\"0\" " +
                "width=\"auto \" " +
                "height=\"auto\"></iframe></body></html>"

        webView.loadDataWithBaseURL(null, htmlCode, "text/html", "utf-8", null)
// height="182.81"
        setContentView(binding.root)

    }
}