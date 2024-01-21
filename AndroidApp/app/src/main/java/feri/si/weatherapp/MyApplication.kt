package feri.si.weatherapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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