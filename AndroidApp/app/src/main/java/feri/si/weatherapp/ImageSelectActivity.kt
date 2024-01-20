package feri.si.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import feri.si.weatherapp.databinding.ActivityImageSelectBinding

class ImageSelectActivity : AppCompatActivity() {
    lateinit var binding: ActivityImageSelectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageSelectBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_image_select)
    }
}