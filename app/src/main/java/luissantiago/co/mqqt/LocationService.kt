package luissantiago.co.mqqt

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import com.google.android.gms.location.*

/**
 * Created by Luis Santiago on 3/3/19.
 */


class LocationService : Service (){

    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        const val UPDATE_INTERVAL = 200
        const val FASTEST_INTERVAL = 400
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }


    @SuppressLint("MissingPermission")
    private fun getLocation(){
        val mLocationRequestHighAccuracy = LocationRequest()
        mLocationRequestHighAccuracy.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequestHighAccuracy.interval = UPDATE_INTERVAL.toLong()
        mLocationRequestHighAccuracy.fastestInterval = FASTEST_INTERVAL.toLong()
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequestHighAccuracy , object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                Toast.makeText(this@LocationService, "Latitude ${p0!!.lastLocation.latitude} Longitude:${p0!!.lastLocation.longitude}", Toast.LENGTH_SHORT).show()
            }
        }, Looper.myLooper())

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getLocation()
        val notificationBuilder: NotificationCompat.Builder
        val mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel("test", "some name", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = "Hello"
            mChannel.enableLights(true)
            mChannel.lightColor = Color.BLUE
            mNotifyManager?.createNotificationChannel(mChannel)
            notificationBuilder = NotificationCompat.Builder(this, mChannel.id)
        } else {
            notificationBuilder = NotificationCompat.Builder(this)
        }

        val notification = notificationBuilder.build()
        startForeground(15, notification)
        return Service.START_STICKY
    }
}