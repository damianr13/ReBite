package rebite.ro.rebiteapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.synthetic.volunteer.activity_currently_in_progress_offers.*
import org.parceler.Parcels
import rebite.ro.rebiteapp.fragments.RestaurantOfferFragment
import rebite.ro.rebiteapp.persistence.PersistenceManager

class CurrentlyInProgressOffersActivity : AppCompatActivity() {

    private lateinit var mOffersListFragment: RestaurantOfferFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currently_in_progress_offers)

        mOffersListFragment = fr_offers_list as RestaurantOfferFragment
        PersistenceManager.getInstance().retrieveInProgressOffersForUser {
            mOffersListFragment.swapRestaurantOffersList(it)
        }

        btn_see_on_map.setOnClickListener {
            val showOnMapIntent = Intent(this,
                    MultipleOffersRouteMapActivity::class.java)
            showOnMapIntent.putExtra(MultipleOffersRouteMapActivity.OFFERS_KEY,
                    ArrayList<Parcelable>().apply {
                        addAll(mOffersListFragment.restaurantOffersList().map {Parcels.wrap(it)})
                    })
            startActivity(showOnMapIntent)
        }
    }
}
