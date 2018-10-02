package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import rebite.ro.rebiteapp.login.facebook.FacebookAuthenticationProvider;
import rebite.ro.rebiteapp.login.google.GoogleAuthenticationProvider;
import rebite.ro.rebiteapp.login.LoginCallbacks;

public class MainActivity extends AppCompatActivity implements LoginCallbacks{

    private FacebookAuthenticationProvider mFacebookAuthProvider;
    private GoogleAuthenticationProvider mGoogleAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFacebookAuthProvider = new FacebookAuthenticationProvider(this, this);
        mGoogleAuthProvider = new GoogleAuthenticationProvider(this, this);
        findViewById(R.id.sign_in_button).setOnClickListener(mGoogleAuthProvider);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GoogleAuthenticationProvider.RC_SIGN_IN) {
            mGoogleAuthProvider.onGoogleSignInActivityResult(data);
            return ;
        }
        mFacebookAuthProvider.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateUI() {
        Intent profileActivityIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileActivityIntent);
    }
}
