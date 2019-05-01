package rebite.ro.rebiteapp

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.volunteer.activity_offer_details.*
import org.parceler.Parcels
import rebite.ro.rebiteapp.maps.GoogleMapController
import rebite.ro.rebiteapp.maps.RouteInfo
import rebite.ro.rebiteapp.maps.RouteListener
import rebite.ro.rebiteapp.offers.RestaurantOffer
import rebite.ro.rebiteapp.utils.PermissionHandler
import rebite.ro.rebiteapp.utils.TimeUtils

class OfferDetailsActivity : AppCompatActivity(), RouteListener {
    private lateinit var mRestaurantOffer: RestaurantOffer

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_details)

        mRestaurantOffer = Parcels.unwrap(intent.getParcelableExtra(OFFER_KEY))

        initViews()

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                .getMapAsync {drawOnMap(it)}
    }

    override fun onRouteRetrieved(routeInfo: RouteInfo) {
        runOnUiThread { tv_eta.text = getString(R.string.eta,
                TimeUtils.formatTimestampAfter(routeInfo.duration.inSeconds)) }
    }

    @SuppressLint("MissingPermission")
    private fun drawOnMap(map: GoogleMap) {
        if (!PermissionHandler.hasAccessToLocation(this)) {
            PermissionHandler.askForAccessToLocation(this)
            return
        }

        map.isMyLocationEnabled = true
        val mapController = GoogleMapController(this, map)
        mapController.startListeningForLocation()

        map.addMarker(MarkerOptions().position(getRestaurantLatLng()))
        mapController.showDirectionsTo(getRestaurantLatLng(), this)
    }

    private fun initViews() {
        Picasso.get().load(mRestaurantOffer.restaurantInfo.image).into(iv_logo)
        tv_name.text = mRestaurantOffer.restaurantInfo.name
        tv_restaurant_description.text = mRestaurantOffer.restaurantInfo.name
        tv_offer_description.text = mRestaurantOffer.description
        tv_number_of_portions_value.text = mRestaurantOffer.quantity.toString()
        tv_pick_up_time.text = getString(R.string.pickup_time_value,
                TimeUtils.format(mRestaurantOffer.pickUpTimestamp))
    }

    private fun getRestaurantLatLng(): LatLng {
        val restaurantLocation = mRestaurantOffer.restaurantInfo.location
        return LatLng(restaurantLocation.latitude, restaurantLocation.longitude)
    }

    companion object {
        const val OFFER_KEY = "offer"
    }
}
