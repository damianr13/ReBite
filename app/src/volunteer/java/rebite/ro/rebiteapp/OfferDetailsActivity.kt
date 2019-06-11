package rebite.ro.rebiteapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.model.TravelMode
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
    private var mMapController: GoogleMapController? = null

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
        runOnUiThread { tv_eta.text = TimeUtils.formatTimestampAfter(routeInfo.duration.inSeconds) }
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
                GoogleMapController(this, map).let {
                    mMapController = it
                    it.startListeningForLocation()

                    val restaurantLatLng = RestaurantOffersManager.getLatLongForOffer(offer)
                    map.addMarker(MarkerOptions().position(restaurantLatLng))

                    it.showDirectionsTo(restaurantLatLng, this)
                }
            }
        }
    }

    private fun initViews() {
        mRestaurantOffer?.let {
            tv_name.text = it.restaurantInfo.name
            tv_offer_description.text = it.description
            tv_number_of_portions.text = it.quantity.toString()
            tv_pick_up_time.text = TimeUtils.format(it.pickUpTimestamp)

            setTransitModeSelectionListener()
            handleActionButtonsVisibility(it)
            setInProgressMarkerListener(it)
            setCompleteMarkerListener(it)
        }
    }
    
    private fun handleActionButtonsVisibility(offer: RestaurantOffer) {
        btn_take_offer.visibility =
                if(offer.state == RestaurantOffer.OfferState.AVAILABLE)
                    View.VISIBLE else
                    View.GONE

        val userInChargeOfOffer = offer.state == RestaurantOffer.OfferState.IN_PROGRESS &&
                offer.assignedUser.email == GeneralProfileInfoProvider.getInstance().email
        btn_mark_complete.visibility = if (userInChargeOfOffer) View.VISIBLE else View.GONE
        
    }
    
    private fun setInProgressMarkerListener(offer: RestaurantOffer) {
        btn_take_offer.setOnClickListener {
            PersistenceManager.getInstance().assignOfferToCurrentUser(offer) {isAssignee ->
                val message = if (isAssignee) getString(R.string.offer_assgined_success) else
                    getString(R.string.offer_assigned_failure)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                PersistenceManager.getInstance().invalidateCache()
                runOnUiThread {
                    finish()
                }
            }
        }
    }
    
    private fun setCompleteMarkerListener(offer: RestaurantOffer) {
        btn_mark_complete.setOnClickListener {
            PersistenceManager.getInstance().markOfferComplete(offer) {isAssignee ->
                val message = if (isAssignee) getString(R.string.offer_marked_complete_success)
                        else getString(R.string.offer_marked_complete_failure)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                PersistenceManager.getInstance().invalidateCache()
                runOnUiThread {
                    finish()
                }
            }
        }
    }
    
    private fun setTransitModeSelectionListener() {
        rg_transit_mode.setOnCheckedChangeListener { _, i ->
            run {
                when (i) {
                    R.id.rb_transit_drive -> triggerDriveMode()
                    R.id.rb_transit_walk -> triggerWalkMode()
                }
            }
        }
    }

    private fun triggerWalkMode() {
        mMapController?.let { it.setTravelModeForDirections(TravelMode.WALKING) }
    }

    private fun triggerDriveMode() {
        mMapController?.let { it.setTravelModeForDirections(TravelMode.DRIVING) }
    }

    companion object {
        const val OFFER_KEY = "offer"
        const val OFFER_ID_KEY ="offerPath"
    }
}
