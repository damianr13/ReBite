package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rebite.ro.rebiteapp.fragments.RestaurantOfferFragment;
import rebite.ro.rebiteapp.offers.RestaurantOffer;
import rebite.ro.rebiteapp.persistence.PersistenceManager;
import rebite.ro.rebiteapp.persistence.UsersManager;
import rebite.ro.rebiteapp.persistence.callbacks.RestaurantOffersRetrieverCallbacks;
import rebite.ro.rebiteapp.state.StateManager;
import rebite.ro.rebiteapp.users.GeneralProfileInfoProvider;
import rebite.ro.rebiteapp.users.ProfileInfoProvider;
import rebite.ro.rebiteapp.utils.UIUtils;

import static rebite.ro.rebiteapp.utils.PublicKeys.LAT_LNG_DESTINATION_KEY;

public class VolunteerProfileActivity extends AppCompatActivity
        implements RestaurantOffersRetrieverCallbacks, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = VolunteerProfileActivity.class.getName();

    @BindView(R.id.iv_profile_picture) ImageView mProfileImageView;
    @BindView(R.id.tv_display_name) TextView mDisplayNameTextView;
    @BindView(R.id.tv_email) TextView mEmailTextView;
    @BindView(R.id.nv_menu) NavigationView mNavigationView;

    private RestaurantOfferFragment mOffersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UIUtils.enableNavDrawerMenu(this);

        PersistenceManager.getInstance().retrieveAllAvailableOffers(this);
        mOffersFragment = (RestaurantOfferFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fr_offers);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    Log.d(TAG, token);
                    Toast.makeText(VolunteerProfileActivity.this, token, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!PersistenceManager.getInstance().isCacheValid()) {
            PersistenceManager.getInstance().retrieveAllAvailableOffers(this);
        }
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

        mNavigationView.setNavigationItemSelectedListener(this);
        return result;
    }

    public void launchMapsActivity() {
        Intent mapsActivityIntent = new Intent(this, MapsActivity.class);
        mapsActivityIntent.putExtra(LAT_LNG_DESTINATION_KEY, provideMockLatLng());
        startActivity(mapsActivityIntent);
    }

    public void launchRestaurantProfileCreatorActivity() {
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

    public void onLogOut() {
        UsersManager.getInstance().logOutCurrentUser();

        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.it_update_times:
                PersistenceManager.getInstance().updateDebugPickupTimes();
                finish();
                return true;
            case R.id.it_log_out:
                onLogOut();
                return true;
            case R.id.it_new_restaurant:
                launchRestaurantProfileCreatorActivity();
                return true;
            case R.id.it_history:
                // TODO: implement history and change this
                launchMapsActivity();
                return true;
            case R.id.it_in_progress_offers:
                Intent launchActivityIntent = new Intent(this,
                        CurrentlyInProgressOffersActivity.class);
                startActivity(launchActivityIntent);
                return true;
        }

        Log.w(TAG, "Unknown menu item: " + menuItem.getTitle().toString());
        return false;
    }
}
