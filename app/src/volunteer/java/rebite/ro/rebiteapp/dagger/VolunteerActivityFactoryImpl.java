package rebite.ro.rebiteapp.dagger;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.parceler.Parcels;

import rebite.ro.rebiteapp.OfferDetailsActivity;
import rebite.ro.rebiteapp.VolunteerProfileActivity;
import rebite.ro.rebiteapp.offers.RestaurantOffer;

import static rebite.ro.rebiteapp.OfferDetailsActivity.OFFER_KEY;

public class VolunteerActivityFactoryImpl implements FlavorSpecificActivityFactory {
    @Override
    public Intent getIntentForProfileActivity(Context context) {
        return new Intent(context, VolunteerProfileActivity.class);
    }

    @Nullable
    @Override
    public Intent getIntentForOfferDetailsActivity(Context context, RestaurantOffer offer) {
        Intent result = new Intent(context, OfferDetailsActivity.class);
        result.putExtra(OFFER_KEY, Parcels.wrap(offer));
        return result;
    }
}
