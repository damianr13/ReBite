package rebite.ro.rebiteapp.dagger;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import rebite.ro.rebiteapp.offers.RestaurantOffer;

public interface FlavorSpecificActivityFactory {
    Intent getIntentForProfileActivity(Context context);
    @Nullable Intent getIntentForOfferDetailsActivity(Context context, RestaurantOffer offer);
}
