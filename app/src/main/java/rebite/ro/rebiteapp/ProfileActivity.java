package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebite.ro.rebiteapp.login.GeneralProfileInfoProvider;
import rebite.ro.rebiteapp.login.ProfileInfoProvider;

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
}
