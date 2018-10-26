package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebite.ro.rebiteapp.login.GeneralProfileInfoProvider;
import rebite.ro.rebiteapp.login.ProfileInfoProvider;

import static rebite.ro.rebiteapp.utils.PublicKeys.LAT_LNG_DESTINATION_KEY;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getName();

    private ProfileInfoProvider profileInfoProvider;

    @BindView(R.id.iv_profile_picture) ImageView profileImageView;
    @BindView(R.id.tv_display_name) TextView displayNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        profileInfoProvider = GeneralProfileInfoProvider.getInstance();

        Picasso.get()
                .load(profileInfoProvider.getProfilePictureUri())
                .into(profileImageView);
        displayNameTextView.setText(profileInfoProvider.getDisplayName());

    }

    @OnClick(R.id.btn_dummy_maps)
    public void launchMapsActivity(View v) {
        Intent mapsActivityIntent = new Intent(this, MapsActivity.class);
        mapsActivityIntent.putExtra(LAT_LNG_DESTINATION_KEY, provideMockLatLng());
        startActivity(mapsActivityIntent);
    }

    private static LatLng provideMockLatLng() {
        return new LatLng(46.225710, 27.670130);
    }
}
