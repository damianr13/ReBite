package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import rebite.ro.rebiteapp.adapters.RestaurantOfferAdapter;
import rebite.ro.rebiteapp.offers.RestaurantOffer;
import rebite.ro.rebiteapp.persistence.PersistenceManager;
import rebite.ro.rebiteapp.persistence.callbacks.RestaurantOffersRetrieverCallbacks;
import rebite.ro.rebiteapp.state.StateManager;
import rebite.ro.rebiteapp.users.GeneralProfileInfoProvider;
import rebite.ro.rebiteapp.users.ProfileInfoProvider;

import static rebite.ro.rebiteapp.utils.PublicKeys.LAT_LNG_DESTINATION_KEY;

public class VolunteerProfileActivity extends AppCompatActivity
        implements RestaurantOffersRetrieverCallbacks{

    private static final String TAG = VolunteerProfileActivity.class.getName();

    @Nullable @BindView(R.id.iv_profile_picture) ImageView mProfileImageView;
    @Nullable @BindView(R.id.tv_display_name) TextView mDisplayNameTextView;
    @Nullable @BindView(R.id.rv_restaurant_offers) RecyclerView mRestaurantOffersRecyclerView;

    private RestaurantOfferAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        ProfileInfoProvider profileInfoProvider = GeneralProfileInfoProvider.getInstance();

        Picasso.get()
                .load(profileInfoProvider.getProfilePictureUri())
                .into(mProfileImageView);
        mDisplayNameTextView.setText(profileInfoProvider.getDisplayName());

        mRestaurantOffersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RestaurantOfferAdapter(this);
        mRestaurantOffersRecyclerView.setAdapter(mAdapter);

        PersistenceManager.getInstance().retrieveAllAvailableOffers(this);
    }

    @Optional
    @OnClick(R.id.btn_dummy_maps)
    public void launchMapsActivity(View v) {
        Intent mapsActivityIntent = new Intent(this, MapsActivity.class);
        mapsActivityIntent.putExtra(LAT_LNG_DESTINATION_KEY, provideMockLatLng());
        startActivity(mapsActivityIntent);
    }

    @Optional
    @OnClick(R.id.btn_add_restaurant_profile)
    public void launchRestaurantProfileCreatorActivity(View v) {
        String authenticatedUserId = GeneralProfileInfoProvider.getInstance().getUid();
        if (authenticatedUserId == null) {
            Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
            return ;
        }

        Intent restaurantProfileCreatorActivityIntent = new Intent(this,
                RestaurantProfileCreatorActivity.class);
        startActivity(restaurantProfileCreatorActivityIntent);
    }

    private static LatLng provideMockLatLng() {
        return new LatLng(46.225710, 27.670130);
    }

    @Override
    public void onRestaurantOffersRetrieved(List<RestaurantOffer> result) {
        mAdapter.swapRestaurantOffers(result);
    }
}
