package rebite.ro.rebiteapp.dagger;

import android.content.Context;
import android.content.Intent;

import rebite.ro.rebiteapp.RestaurantProfileActivity;
import rebite.ro.rebiteapp.offers.RestaurantOffer;

public class RestaurantActivityFactoryImpl implements RestaurantActivityFactory {
    @Override
    public Intent getIntentForProfileActivity(Context context) {
        return new Intent(context, RestaurantProfileActivity.class);
    }

    @Override
    public Intent getIntentForOfferDetailsActivity(Context context, RestaurantOffer offer) {
        return null;
    }
}
