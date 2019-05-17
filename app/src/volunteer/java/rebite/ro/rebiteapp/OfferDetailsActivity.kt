package rebite.ro.rebiteapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.volunteer.activity_offer_details.*
import org.parceler.Parcels
import rebite.ro.rebiteapp.maps.GoogleMapController
import rebite.ro.rebiteapp.maps.RouteInfo
import rebite.ro.rebiteapp.maps.RouteListener
import rebite.ro.rebiteapp.offers.RestaurantOffer
import rebite.ro.rebiteapp.persistence.PersistenceManager
import rebite.ro.rebiteapp.users.GeneralProfileInfoProvider
import rebite.ro.rebiteapp.utils.PermissionHandler
import rebite.ro.rebiteapp.utils.RestaurantOffersManager
import rebite.ro.rebiteapp.utils.TimeUtils

class OfferDetailsActivity : AppCompatActivity(), RouteListener {
    private var mRestaurantOffer: RestaurantOffer? = null
    private var mMap: GoogleMap? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_details)

        if (intent.hasExtra(OFFER_KEY)) {
            mRestaurantOffer = Parcels.unwrap(intent.getParcelableExtra(OFFER_KEY))
            initViews()
            drawOnMap()
        }
        else if (intent.hasExtra(OFFER_ID_KEY)) {
            PersistenceManager.getInstance().retrieveSpecificOffer(intent.getStringExtra(OFFER_ID_KEY)) {
                if (it.isEmpty()) {
                    return@retrieveSpecificOffer
                }
                mRestaurantOffer = it[0]
                initViews()
                drawOnMap()
            }
        }

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                .getMapAsync {
                    setMap(it)
                    drawOnMap()
                }
    }

    override fun onRouteRetrieved(routeInfo: RouteInfo) {
        runOnUiThread { tv_eta.text = getString(R.string.eta,
                TimeUtils.formatTimestampAfter(routeInfo.duration.inSeconds)) }
    }

    private fun setMap(map: GoogleMap) {
        mMap = map
    }

    @SuppressLint("MissingPermission")
    private fun drawOnMap() {
        mMap?.let { map ->
            mRestaurantOffer?.let {offer ->
                if (!PermissionHandler.hasAccessToLocation(this)) {
                    PermissionHandler.askForAccessToLocation(this)
                    return@drawOnMap
                }

                map.isMyLocationEnabled = true
                val mapController = GoogleMapController(this, map)
                mapController.startListeningForLocation()

                val restaurantLatLng = RestaurantOffersManager.getLatLongForOffer(offer)
                map.addMarker(MarkerOptions().position(restaurantLatLng))
                mapController.showDirectionsTo(restaurantLatLng, this)
            }
        }
    }

    private fun initViews() {
        mRestaurantOffer?.let {
            Picasso.get().load(it.restaurantInfo.image).into(iv_logo)
            tv_name.text = it.restaurantInfo.name
            tv_restaurant_description.text = it.restaurantInfo.name
            tv_offer_description.text = it.description
            tv_number_of_portions_value.text = it.quantity.toString()
            tv_pick_up_time.text = getString(R.string.pickup_time_value,
                    TimeUtils.format(it.pickUpTimestamp))

            btn_take_offer.visibility =
                    if(it.state == RestaurantOffer.OfferState.AVAILABLE)
                        View.VISIBLE else
                        View.GONE

            val userInChargeOfOffer = it.state == RestaurantOffer.OfferState.IN_PROGRESS &&
                    it.assignedUser.email == GeneralProfileInfoProvider.getInstance().email
            btn_mark_complete.visibility = if (userInChargeOfOffer) View.VISIBLE else View.GONE

            btn_take_offer.setOnClickListener {view ->
                PersistenceManager.getInstance().assignOfferToCurrentUser(it) {isAssignee ->
                    val message = if (isAssignee) getString(R.string.offer_assgined_success) else
                        getString(R.string.offer_assigned_failure)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    PersistenceManager.getInstance().invalidateCache()
                    runOnUiThread {
                        finish()
                    }
                }
            }

            btn_mark_complete.setOnClickListener {view ->
                PersistenceManager.getInstance().markOfferComplete(it) {isAssignee ->
                    val message = if (isAssignee) getString(R.string.offer_marked_complete_success) else
                        getString(R.string.offer_marked_complete_failure)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    PersistenceManager.getInstance().invalidateCache()
                    runOnUiThread {
                        finish()
                    }
                }
            }
        }
    }

    companion object {
        const val OFFER_KEY = "offer"
        const val OFFER_ID_KEY ="offerPath"
    }
}
