package rebite.ro.rebiteapp.login.google;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import rebite.ro.rebiteapp.R;
import rebite.ro.rebiteapp.login.AuthenticationProvider;
import rebite.ro.rebiteapp.login.LoginCallbacks;
import rebite.ro.rebiteapp.login.ProfileInfoProvider;

public class GoogleAuthenticationProvider extends AuthenticationProvider implements View.OnClickListener{

    private static final String TAG = GoogleAuthenticationProvider.class.getName();

    public static final int RC_SIGN_IN = 13;

    private Activity mActivity;
    private GoogleSignInClient mGoogleSignInClient;

    public GoogleAuthenticationProvider(Activity activity, LoginCallbacks loginCallbacks) {
        super(activity, loginCallbacks);
        mActivity = activity;

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    @Override
    public void onClick(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onGoogleSignInActivityResult(Intent data) {
        Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(mActivity, "Successfully signed in with google!", Toast.LENGTH_SHORT)
                    .show();
            handleFirebaseSignIn(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    protected AuthCredential extractFirebaseCredential(Object credentialProvider) {
        GoogleSignInAccount googleAccount = (GoogleSignInAccount) credentialProvider;
        return GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
    }

    @Override
    protected ProfileInfoProvider buildProfileInfoProvider() {
        return new GoogleProfileInfoProvider(mActivity.getApplicationContext());
    }
}
