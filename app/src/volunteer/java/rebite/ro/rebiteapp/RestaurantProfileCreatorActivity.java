package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebite.ro.rebiteapp.persistence.UsersManager;

import static rebite.ro.rebiteapp.MapLocationSelectorActivity.ADDRESS_KEY;

public class RestaurantProfileCreatorActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 301;
    private static final int ADDRESS_SELECTOR_REQUEST_CODE = 302;

    private static final String TAG = RestaurantProfileCreatorActivity.class.getName();

    @BindView(R.id.et_username) EditText mUsernameEditText;
    @BindView(R.id.et_password) EditText mPasswordEditText;
    @BindView(R.id.et_confirm_password) EditText mConfirmPasswordEditText;
    @BindView(R.id.et_place_autocomplete) EditText mPlaceAutocomplete;


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
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String passwordConfirmation = mConfirmPasswordEditText.getText().toString();

        if (!password.equals(passwordConfirmation)) {
            Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return ;
        }

        UsersManager.getInstance().createNewUser(this, username, password);
        onBackPressed();
    }

    @OnClick(R.id.et_place_autocomplete)
    public void launchPlacesAutocompleteActivity(View v) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
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
                String address = data.getStringExtra(ADDRESS_KEY);
                setRestaurantAddress(address);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
