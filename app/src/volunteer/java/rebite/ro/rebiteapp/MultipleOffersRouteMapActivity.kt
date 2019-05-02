package rebite.ro.rebiteapp

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.volunteer.activity_multiple_offers_route_map.*
import org.parceler.Parcels
import rebite.ro.rebiteapp.maps.GoogleMapController
import rebite.ro.rebiteapp.maps.RouteInfo
import rebite.ro.rebiteapp.maps.RouteListener
import rebite.ro.rebiteapp.offers.RestaurantOffer
import rebite.ro.rebiteapp.utils.PermissionHandler
import rebite.ro.rebiteapp.utils.RestaurantOffersManager

class MultipleOffersRouteMapActivity : AppCompatActivity() {

    private lateinit var mDisplayedOfferList: List<RestaurantOffer>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_offers_route_map)

        mDisplayedOfferList = ArrayList()
        intent.getParcelableArrayListExtra<Parcelable>(OFFERS_KEY).let { parcelables ->
            parcelables.forEach {
                mDisplayedOfferList = mDisplayedOfferList.plus(Parcels.unwrap(it) as RestaurantOffer)
            }
        }

        (map as SupportMapFragment).getMapAsync {
            drawOnMap(it)
        }
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

        val wayPoints = ArrayList<LatLng>()
        mDisplayedOfferList.forEach {
            val currentWayPoint = RestaurantOffersManager.getLatLongForOffer(it)
            wayPoints.add(currentWayPoint)
            map.addMarker(MarkerOptions().position(currentWayPoint))
        }

        mapController.showDirectionsThrough(wayPoints, object : RouteListener {
            override fun onRouteRetrieved(routeInfo: RouteInfo) {
                //TODO: de stabilit ce fac cu informatia asta
            }
        })
    }

    companion object {
        const val OFFERS_KEY = "OFFERS"
    }

}
