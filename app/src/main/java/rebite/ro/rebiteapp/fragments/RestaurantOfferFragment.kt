package rebite.ro.rebiteapp.fragments

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_restaurantoffer.view.*
import org.parceler.Parcels
import rebite.ro.rebiteapp.R
import rebite.ro.rebiteapp.adapters.RestaurantOfferAdapter
import rebite.ro.rebiteapp.dagger.AbstractMainApplication
import rebite.ro.rebiteapp.dagger.FlavorSpecificActivityFactory
import rebite.ro.rebiteapp.offers.RestaurantOffer
import javax.inject.Inject

class RestaurantOfferFragment : Fragment(), RestaurantOfferAdapter.IOfferClickListener {
    private lateinit var mRestaurantOfferAdapter : RestaurantOfferAdapter
    fun restaurantOffersList(): List<RestaurantOffer> = mRestaurantOfferAdapter.offersList

    @Inject lateinit var mFlavorSpecificActivityFactory: FlavorSpecificActivityFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            (it.application as AbstractMainApplication)
                    .flavorSpecificComponent.inject(this)
        }

        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_restaurantoffer, container, false)
        val restaurantOfferList = arguments?.getParcelableArrayList<Parcelable>(OFFERS_KEY)
                ?.map { Parcels.unwrap(it) as RestaurantOffer}

        mRestaurantOfferAdapter = RestaurantOfferAdapter(context, this)
        mRestaurantOfferAdapter.swapRestaurantOffers(restaurantOfferList)
        val llm = LinearLayoutManager(context)
        view.rv_past_offers.apply {
            layoutManager = llm
            adapter = mRestaurantOfferAdapter
        }

        return view
    }

    fun swapRestaurantOffersList(restaurantOffersList: List<RestaurantOffer>) {
        mRestaurantOfferAdapter.swapRestaurantOffers(restaurantOffersList)
    }

    override fun onOfferSelected(offer: RestaurantOffer) {
        val offerDetailsIntent = mFlavorSpecificActivityFactory
                .getIntentForOfferDetailsActivity(context, offer)
        offerDetailsIntent?: return

        startActivity(offerDetailsIntent)
    }

    companion object {
        const val OFFERS_KEY = "offers"
        @JvmStatic
        fun newInstance(restaurantOffersList : List<RestaurantOffer>) =
                RestaurantOfferFragment().apply {
                    arguments = Bundle().apply {
                        val parcelableOffers = ArrayList<Parcelable>().apply {
                           addAll(restaurantOffersList.map {Parcels.wrap(it)})
                        }
                        putParcelableArrayList(OFFERS_KEY, parcelableOffers)
                    }
                }
    }
}
