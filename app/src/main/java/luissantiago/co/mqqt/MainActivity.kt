package luissantiago.co.mqqt

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class MainActivity : Activity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    companion object {
        const val REQUEST_PERMISSION = 38
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkMapsServices()) {
            if (checkForPermissionsLocation()) {
                Toast.makeText(this, "We got location and play services", Toast.LENGTH_SHORT).show()
                getLastKnowLocation()
                val intent = Intent(this@MainActivity, LocationService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastKnowLocation() {
        mFusedLocationClient.lastLocation.addOnCompleteListener(object : OnCompleteListener<Location> {
            override fun onComplete(p0: Task<Location>) {
                if (p0.isSuccessful) {
                    val location = p0.result
                    if(location != null){
                        Toast.makeText(this@MainActivity, "Latitude ${location!!.latitude} Longitude:${location!!.longitude}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun checkForPermissionsLocation(): Boolean {
        if (ContextCompat.checkSelfPermission(this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION)
        }

        return false
    }


    private fun checkMapsServices(): Boolean {
        if (isServiceAvailable()) {
            if (isMapsEnabled()) {
                return true
            }
        }
        return false
    }

    private fun isServiceAvailable(): Boolean {
        val availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (availability == ConnectionResult.SUCCESS) {
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(availability)) {
            val dialogue = GoogleApiAvailability.getInstance().getErrorDialog(this, availability, 2)
            dialogue.show()
        } else {
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO: Request the location
                }
            }
        }
    }


    private fun isMapsEnabled(): Boolean {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
        return true
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("This application requieres gps to work properly")
                .setCancelable(false)
                .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        val enableGpsIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivityForResult(enableGpsIntent, REQUEST_PERMISSION)
                    }
                })

        val alert = builder.create()
        alert.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                checkForPermissionsLocation()
            }
        }
    }
}
