package rebite.ro.rebiteapp

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.volunteer.activity_offer_details.*
import org.parceler.Parcels
import rebite.ro.rebiteapp.maps.GoogleMapController
import rebite.ro.rebiteapp.offers.RestaurantOffer
import rebite.ro.rebiteapp.utils.PermissionHandler

class OfferDetailsActivity : AppCompatActivity() {

    private lateinit var mRestaurantOffer: RestaurantOffer

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_details)

        mRestaurantOffer = Parcels.unwrap(intent.getParcelableExtra(OFFER_KEY))

        Picasso.get().load(mRestaurantOffer.restaurantInfo.image).into(iv_logo)
        tv_name.text = mRestaurantOffer.restaurantInfo.name
        tv_restaurant_description.text = mRestaurantOffer.restaurantInfo.name
        tv_offer_description.text = mRestaurantOffer.description
        tv_number_of_portions_value.text = mRestaurantOffer.quantity.toString()

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync {
                if (!PermissionHandler.hasAccessToLocation(this)) {
                    PermissionHandler.askForAccessToLocation(this)
                    return@getMapAsync
                }

                it.isMyLocationEnabled = true
                val mapController = GoogleMapController(this, it)
                mapController.startListeningForLocation()

                it.addMarker(MarkerOptions().position(getRestaurantLatLng()))
                mapController.showDirectionsTo(getRestaurantLatLng())
        }
    }

    private fun getRestaurantLatLng(): LatLng {
        val restaurantLocation = mRestaurantOffer.restaurantInfo.location
        return LatLng(restaurantLocation.latitude, restaurantLocation.longitude)
    }

    companion object {
        const val OFFER_KEY = "offer"
    }
}
