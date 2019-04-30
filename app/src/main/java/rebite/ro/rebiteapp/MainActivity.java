package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.PendingResult;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import rebite.ro.rebiteapp.dagger.AbstractMainApplication;
import rebite.ro.rebiteapp.dagger.FlavorSpecificActivityFactory;
import rebite.ro.rebiteapp.login.LoginCallbacks;
import rebite.ro.rebiteapp.login.cache.CacheProfileInfoManager;
import rebite.ro.rebiteapp.login.email.EmailAuthenticationProvider;
import rebite.ro.rebiteapp.login.facebook.FacebookAuthenticationProvider;
import rebite.ro.rebiteapp.login.google.GoogleAuthenticationProvider;
import rebite.ro.rebiteapp.persistence.PersistenceManager;
import rebite.ro.rebiteapp.state.StateManager;
import rebite.ro.rebiteapp.users.GeneralProfileInfoProvider;
import rebite.ro.rebiteapp.users.ProfileInfoProvider;
import rebite.ro.rebiteapp.users.UserInfo;

public class MainActivity extends AppCompatActivity implements LoginCallbacks{

    private FacebookAuthenticationProvider mFacebookAuthProvider;
    private GoogleAuthenticationProvider mGoogleAuthProvider;

    @Inject
    FlavorSpecificActivityFactory mActivityFactory;

    @Nullable @BindView(R.id.btn_facebook_login) LoginButton mFacebookLoginButton;
    @Nullable @BindView(R.id.btn_google_login) SignInButton mGoogleLoginButton;

    @Nullable @BindView(R.id.et_email) EditText mUsernameEditText;
    @Nullable @BindView(R.id.et_password) EditText mPasswordEditText;
    @Nullable @BindView(R.id.btn_email_login) Button mButton;
    @Nullable @BindView(R.id.rl_loader) View mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ((AbstractMainApplication) getApplication())
                .getFlavorSpecificComponent()
                .inject(MainActivity.this);

        if (isVolunteerApp()) {
            mFacebookAuthProvider = new FacebookAuthenticationProvider(this, this);
            mFacebookLoginButton.setReadPermissions(FacebookAuthenticationProvider.EMAIL_KEY);

            mGoogleAuthProvider = new GoogleAuthenticationProvider(this, this);
            mGoogleLoginButton.setOnClickListener(mGoogleAuthProvider);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            blockInput();
            ProfileInfoProvider profileInfoProvider = new CacheProfileInfoManager(this)
                    .retrieveCachedProfileInfoProvider();
            if (profileInfoProvider == null) {
                allowInput();
                return ;
            }
            GeneralProfileInfoProvider.getInstance().setProfileInfoProvider(profileInfoProvider);
            onSignInComplete();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GoogleAuthenticationProvider.RC_SIGN_IN) {
            mGoogleAuthProvider.onGoogleSignInActivityResult(data);
            return ;
        }
        mFacebookAuthProvider.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isVolunteerApp() {
        return BuildConfig.FLAVOR.equals("volunteer");
    }

    private void allowInput() {
        if (mLoader != null) {
            mLoader.setClickable(false);
            mLoader.setVisibility(View.INVISIBLE);
        }
    }

    private void blockInput() {
        if (mLoader != null) {
            mLoader.setVisibility(View.VISIBLE);
            mLoader.setClickable(true);
        }
    }

    @Override
    public void onSignInFailed() {
        allowInput();
    }

    @Override
    public void onSignInComplete() {
        final Intent startProfileActivityIntent = mActivityFactory
                .getIntentForProfileActivity(this);

        new CacheProfileInfoManager(this).cacheProfileInfoProvider(
                GeneralProfileInfoProvider.getInstance());
        PersistenceManager.getInstance().synchronizeCurrentUser(this);

        PersistenceManager.getInstance().retrieveExtraUserInfo(
                new PendingResult.Callback<UserInfo>() {
                    @Override
                    public void onResult(UserInfo result) {
                        // no extra info about this user
                        if (result == null) {
                            return ;
                        }

                        StateManager.getInstance().setExtraUserInfo(result);

                        runOnUiThread(MainActivity.this::allowInput);
                        ActivityCompat.finishAffinity(MainActivity.this);
                        startActivity(startProfileActivityIntent);

                        boolean isAdmin = result.getCurrentUserRoles().contains(UserInfo.Role.ADMIN);
                        if (!isAdmin) {
                            return ;
                        }

                        Toast.makeText(MainActivity.this,
                                "Has admin rights!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }
                });
    }

    @Optional
    @OnClick(R.id.btn_email_login)
    @SuppressWarnings("ConstantConditions")
    public void loginWithCredentials(View v) {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if ("".equals(username) || "".equals(password)) {
            Toast.makeText(this, R.string.credentials_expected,
                    Toast.LENGTH_SHORT).show();
            return ;
        }

        this.runOnUiThread(this::blockInput);
        new EmailAuthenticationProvider(this, this)
                .loginUserWithCredentials(username, password);
    }
}
