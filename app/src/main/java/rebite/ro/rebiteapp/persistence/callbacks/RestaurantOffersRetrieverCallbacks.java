package rebite.ro.rebiteapp.persistence.callbacks;

import java.util.List;

import rebite.ro.rebiteapp.offers.RestaurantOffer;

public interface RestaurantOffersRetrieverCallbacks {
    void onRestaurantOffersRetrieved(List<RestaurantOffer> result);
}
