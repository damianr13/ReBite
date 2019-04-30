package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
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
import rebite.ro.rebiteapp.fragments.RestaurantOfferFragment;
import rebite.ro.rebiteapp.offers.RestaurantOffer;
import rebite.ro.rebiteapp.persistence.PersistenceManager;
import rebite.ro.rebiteapp.persistence.UsersManager;
import rebite.ro.rebiteapp.persistence.callbacks.RestaurantOffersRetrieverCallbacks;
import rebite.ro.rebiteapp.state.StateManager;
import rebite.ro.rebiteapp.users.GeneralProfileInfoProvider;
import rebite.ro.rebiteapp.users.ProfileInfoProvider;

import static rebite.ro.rebiteapp.utils.PublicKeys.LAT_LNG_DESTINATION_KEY;

public class VolunteerProfileActivity extends AppCompatActivity
        implements RestaurantOffersRetrieverCallbacks{

    private static final String TAG = VolunteerProfileActivity.class.getName();

    @BindView(R.id.iv_profile_picture) ImageView mProfileImageView;
    @BindView(R.id.tv_display_name) TextView mDisplayNameTextView;
    @BindView(R.id.tv_email) TextView mEmailTextView;

    private RestaurantOfferFragment mOffersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        DrawerLayout dl = findViewById(R.id.dl_container);
        ActionBarDrawerToggle t = new ActionBarDrawerToggle(this, dl,
                R.string.nav_drawer_open, R.string.nav_drawer_close);

        dl.addDrawerListener(t);
        t.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        PersistenceManager.getInstance().retrieveAllAvailableOffers(this);
        mOffersFragment = (RestaurantOfferFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fr_offers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        ButterKnife.bind(this);

        ProfileInfoProvider profileInfoProvider = GeneralProfileInfoProvider.getInstance();

        Picasso.get()
                .load(profileInfoProvider.getProfilePictureUri())
                .into(mProfileImageView);
        mDisplayNameTextView.setText(profileInfoProvider.getDisplayName());
        mEmailTextView.setText(profileInfoProvider.getEmail());

        return result;
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
        if (authenticatedUserId == null || !StateManager.getInstance().hasAdminRights()) {
            Toast.makeText(this, R.string.permission_not_granted,
                    Toast.LENGTH_SHORT).show();
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
        Log.i(TAG, "Received " + result.size() + " offers");
        mOffersFragment.swapRestaurantOffersList(result);
    }

    @OnClick(R.id.btn_log_out)
    public void onLogOut(View v) {
        UsersManager.getInstance().logOutCurrentUser();

        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
