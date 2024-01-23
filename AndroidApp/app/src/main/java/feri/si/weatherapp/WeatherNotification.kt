package feri.si.weatherapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class WeatherNotification( private  val context: Context) {
    companion object{
        val WEATHER_CHANNEL_ID = "WeatherNotificationChannel"
    }
    private val notificationmanager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(ratio: String){
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(context,
            1,
            activityIntent,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val activityIntentPhoto = Intent(context, InputActivity::class.java)
        val activityPendingIntentPhoto = PendingIntent.getActivity(context,
            1,
            activityIntentPhoto,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = NotificationCompat.Builder(context, WEATHER_CHANNEL_ID)
            .setSmallIcon(R.drawable.cloud)
            .setContentTitle("Oblacnost")
            .setContentText("V vasi okolici je zmerna oblacnost($ratio %)")
            .setContentIntent(activityPendingIntent)
            .addAction(
                R.drawable.baseline_photo_camera_24,
                "Dodaj svojo sliko",
                activityPendingIntentPhoto
            )
            .build()

        notificationmanager.notify(1,notification)
    }
}