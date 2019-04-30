package rebite.ro.rebiteapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.Circle;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.type.LatLng;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebite.ro.rebiteapp.persistence.PersistenceManager;
import rebite.ro.rebiteapp.persistence.UsersManager;
import rebite.ro.rebiteapp.users.UserInfo;
import rebite.ro.rebiteapp.users.restaurants.RestaurantInfo;
import rebite.ro.rebiteapp.utils.LocationUtils;
import rebite.ro.rebiteapp.utils.PermissionHandler;

import static rebite.ro.rebiteapp.MapLocationSelectorActivity.ADDRESS_KEY;
import static rebite.ro.rebiteapp.MapLocationSelectorActivity.LOCATION_KEY;

public class RestaurantProfileCreatorActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 301;
    private static final int ADDRESS_SELECTOR_REQUEST_CODE = 302;

    private static final int PLACES_AUTOCOMPLETE_RADIUS = 10000;

    private static final String TAG = RestaurantProfileCreatorActivity.class.getName();

    @BindView(R.id.et_email) EditText mEmailEditText;
    @BindView(R.id.et_password) EditText mPasswordEditText;
    @BindView(R.id.et_confirm_password) EditText mConfirmPasswordEditText;
    @BindView(R.id.et_place_autocomplete) EditText mPlaceAutocomplete;

    private Location mSelectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile_creator);

        ButterKnife.bind(this);
        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
    }

    @OnClick(R.id.btn_create)
    public void createRestaurantProfile(View view) {
        String username = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String passwordConfirmation = mConfirmPasswordEditText.getText().toString();

        if (!password.equals(passwordConfirmation)) {
            Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return ;
        }

        if (mSelectedLocation == null) {
            Toast.makeText(this, R.string.select_location_restaurant, Toast.LENGTH_SHORT)
                    .show();
            return ;
        }

        UsersManager.getInstance().createNewUser(this, username, password)
            .addOnCompleteListener((t) -> {
                    if (!t.isSuccessful() || t.getResult() == null) {
                        return ;
                    }
                    String createdUserId = t.getResult().getUser().getUid();
                    RestaurantInfo restaurantInfo = readInfoFromFields();
                    UserInfo userInfo = new UserInfo(mEmailEditText.getText().toString(),
                            restaurantInfo);
                    PersistenceManager.getInstance().persistUserWithInfo(
                        RestaurantProfileCreatorActivity.this, createdUserId, userInfo);
                }
            );
        onBackPressed();
    }

    private RestaurantInfo readInfoFromFields() {
        RestaurantInfo result = new RestaurantInfo();
        result.address = mPlaceAutocomplete.getText().toString();
        result.name = "Dummy name";
        result.description = "Dummy Description";
        result.image = "https://picsum.photos/id/114/300/300";
        result.location = mSelectedLocation;

        return result;
    }

    @OnClick(R.id.et_place_autocomplete)
    public void launchPlacesAutocompleteActivity(View v) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                    .setCountry("ro")
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setLocationBias()
                    .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    public LocationBias buildPlacesLocationBias() {
        if (!PermissionHandler.hasAccessToLocation(this)) {
            PermissionHandler.askForAccessToLocation(this);
        }

        if (!PermissionHandler.hasAccessToLocation(this)) {
            return null;
        }
        Location currentLocation = LocationUtils.getCurrentUserLocation(this);
        if (currentLocation == null) {
            return null;
        }

        LatLng southWest = LatLng.newBuilder()
                .setLatitude(currentLocation.getLatitude() - PLACES_AUTOCOMPLETE_RADIUS / 2)
                .setLongitude(currentLocation.getLongitude() - PLACES_AUTOCOMPLETE_RADIUS / 2)
                .build();
        LatLng northEast = LatLng.newBuilder()
                .setLatitude(currentLocation.getLatitude() + PLACES_AUTOCOMPLETE_RADIUS / 2)
                .setLongitude(currentLocation.getLongitude() + PLACES_AUTOCOMPLETE_RADIUS / 2)
                .build();
        new Circle();
        return RectangularBounds.newInstance(currentLocation.lat)
//        return "location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() +
//                "&radius=" + PLACES_AUTOCOMPLETE_RADIUS;
    }

    @OnClick(R.id.btn_choose_location)
    public void launchMapLocationSelectorActivity(View v) {
        Intent intent = new Intent(this, MapLocationSelectorActivity.class);
        startActivityForResult(intent, ADDRESS_SELECTOR_REQUEST_CODE);
    }

    private void setRestaurantAddress(String address) {
        runOnUiThread(() -> mPlaceAutocomplete.setText(address));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) {
            return ;
        }

        switch (requestCode) {
            case AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    setRestaurantAddress(place.getAddress());

                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.e(TAG, status.getStatusMessage());
                }
                break;
            case ADDRESS_SELECTOR_REQUEST_CODE:
                mSelectedLocation = data.getParcelableExtra(LOCATION_KEY);
                String address = data.getStringExtra(ADDRESS_KEY);
                setRestaurantAddress(address);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
