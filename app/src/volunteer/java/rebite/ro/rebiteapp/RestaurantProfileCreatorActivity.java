package rebite.ro.rebiteapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebite.ro.rebiteapp.persistence.UsersManager;
import rebite.ro.rebiteapp.users.RestaurantUserCreatorRequest;
import rebite.ro.rebiteapp.users.restaurants.SimpleLocation;

import static rebite.ro.rebiteapp.MapLocationSelectorActivity.ADDRESS_KEY;
import static rebite.ro.rebiteapp.MapLocationSelectorActivity.LOCATION_KEY;

public class RestaurantProfileCreatorActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 301;
    private static final int ADDRESS_SELECTOR_REQUEST_CODE = 302;

    private static final String TAG = RestaurantProfileCreatorActivity.class.getName();

    @BindView(R.id.et_email) EditText mEmailEditText;
    @BindView(R.id.et_display_name) EditText mDisplayNameEditText;
    @BindView(R.id.et_description) EditText mDescriptionEditText;
    @BindView(R.id.et_place_autocomplete) EditText mPlaceAutocomplete;
    @BindView(R.id.et_image_url) EditText mImageURLEditText;
    @BindView(R.id.iv_image) ImageView mLoadedImageView;

    private Location mSelectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile_creator);

        ButterKnife.bind(this);
        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));

        mImageURLEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString();
                if (!URLUtil.isValidUrl(value)) {
                    return ;
                }
                Picasso.get().load(value).into(mLoadedImageView);
            }
        });
    }

    @OnClick(R.id.btn_create)
    public void createRestaurantProfile(View view) {
        if (mSelectedLocation == null) {
            Toast.makeText(this, R.string.select_location_restaurant, Toast.LENGTH_SHORT)
                    .show();
            return ;
        }
        RestaurantUserCreatorRequest request = new RestaurantUserCreatorRequest();
        request.email = mEmailEditText.getText().toString();
        request.display_name = mDisplayNameEditText.getText().toString();
        request.description = mDescriptionEditText.getText().toString();
        request.image = mImageURLEditText.getText().toString();
        request.location = new SimpleLocation(mSelectedLocation);
        request.address = mPlaceAutocomplete.getText().toString();

        UsersManager.getInstance().postRequestForNewRestaurantAccount(this, request)
            .addOnCompleteListener((t) -> {
                    if (!t.isSuccessful() || t.getResult() == null) {
                        return ;
                    }

                    Toast.makeText(RestaurantProfileCreatorActivity.this,
                            "Request sent successfully", Toast.LENGTH_SHORT).show();
                }
            );
        onBackPressed();
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
                    .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
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
