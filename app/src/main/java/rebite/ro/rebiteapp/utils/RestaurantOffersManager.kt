package rebite.ro.rebiteapp.utils

import rebite.ro.rebiteapp.offers.RestaurantOffer

class RestaurantOffersManager {
    companion object {
        val offerExpired = {o: RestaurantOffer -> o.pickUpTimestamp < System.currentTimeMillis()}

        fun filterExpiredOffers(offers : List<RestaurantOffer>): List<RestaurantOffer> {
            return offers.filter { offerExpired(it)}
        }

        fun filterAvailableOffers(offers : List<RestaurantOffer>): List<RestaurantOffer> {
            return offers.filter { !offerExpired(it)}
        }
    }
}