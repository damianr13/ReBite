package rebite.ro.rebiteapp.utils

import com.google.android.gms.maps.model.LatLng
import rebite.ro.rebiteapp.offers.RestaurantOffer

class RestaurantOffersManager {
    companion object {
        val offerExpired = {o: RestaurantOffer -> o.pickUpTimestamp < System.currentTimeMillis()}

        fun filterExpiredOffers(offers : List<RestaurantOffer>): List<RestaurantOffer> {
            return offers.filter { offerExpired(it)}
        }

        fun filterAvailableOffers(offers : List<RestaurantOffer>): List<RestaurantOffer> {
            return offers.filter { it.state == RestaurantOffer.OfferState.AVAILABLE && !offerExpired(it)}
        }

        fun filterInProgressOffers(offers: List<RestaurantOffer>): List<RestaurantOffer> {
            return offers.filter { it.state == RestaurantOffer.OfferState.IN_PROGRESS}
        }

        fun getLatLongForOffer(offer: RestaurantOffer): LatLng {
            val restaurantLocation = offer.restaurantInfo.location
            return LatLng(restaurantLocation.latitude, restaurantLocation.longitude)
        }
    }
}