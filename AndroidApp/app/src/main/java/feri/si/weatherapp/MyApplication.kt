package feri.si.weatherapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import java.util.UUID

const val MY_SP_FILE_NAME = "myshareddata"

class MyApplication: Application() {
    lateinit var sharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        initShared()
        if (!containsID()) {
            saveID(UUID.randomUUID().toString().replace("-", ""))
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                WeatherNotification.WEATHER_CHANNEL_ID,
                "Weather",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Obvestilo o oblacnosti v okolici"
            val notificationmanager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationmanager.createNotificationChannel(channel)
        }
    }

    fun initShared() {
        sharedPref = getSharedPreferences(MY_SP_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun containsID():Boolean {
        return sharedPref.contains("ID")
    }

    fun saveID(id:String) {
        with (sharedPref.edit()) {
            putString("ID", id)
            apply()
        }
    }

    fun getID(): String? {
        return sharedPref.getString("ID","DefaultNoData")
    }
}