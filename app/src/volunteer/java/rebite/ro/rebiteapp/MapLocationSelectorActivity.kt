package rebite.ro.rebiteapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.volunteer.activity_map_location_selector.*
import rebite.ro.rebiteapp.maps.GoogleMapController
import rebite.ro.rebiteapp.services.FetchAddressIntentService
import rebite.ro.rebiteapp.utils.PermissionHandler

class MapLocationSelectorActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mGoogleMap : GoogleMap
    private val mAddressResultReceiver = AddressResultReceiver()

    private lateinit var mSelectedLocation : Location
    private lateinit var mSelectedAddress : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_location_selector)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        btn_confirm_location.setOnClickListener {
            val data = Intent()
            data.putExtra(LOCATION_KEY, mSelectedLocation)
            data.putExtra(ADDRESS_KEY, mSelectedAddress)

            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        if (!PermissionHandler.hasAccessToLocation(this)) {
            PermissionHandler.askForAccessToLocation(this)
            onMapReady(googleMap)
            return
        }

        mGoogleMap.isMyLocationEnabled = true
        GoogleMapController(this, mGoogleMap).animateMapToCurrentLocation()

        mGoogleMap.setOnCameraIdleListener {
            val center = mGoogleMap.cameraPosition.target
            mSelectedLocation = Location(LocationManager.GPS_PROVIDER)
            mSelectedLocation.latitude = center.latitude
            mSelectedLocation.longitude = center.longitude

            val intent = Intent(this, FetchAddressIntentService::class.java).apply {
                putExtra(FetchAddressIntentService.Constants.RECEIVER, mAddressResultReceiver)
                putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mSelectedLocation)
            }
            startService(intent)
        }
    }

    fun updateEditText(value: String) {
        et_place.setText(value)
    }

    internal inner class AddressResultReceiver : ResultReceiver(Handler()) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            val addressOutput = resultData
                    ?.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY) ?: ""
            mSelectedAddress = addressOutput
            updateEditText(addressOutput)
        }
    }

    companion object {
        const val LOCATION_KEY = "location"
        const val ADDRESS_KEY = "address"
    }
}
