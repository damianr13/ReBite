package rebite.ro.rebiteapp.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class FacebookAuthenticationProvider extends AuthenticationProvider{

    private static final String TAG = FacebookAuthenticationProvider.class.getName();

    private CallbackManager mCallbackManager;
    private Context mContext;
    private FirebaseAuth mAuth;

    public FacebookAuthenticationProvider(Context context, LoginCallbacks loginCallbacks) {
        super(context, loginCallbacks);
        mContext = context;
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        registerLoginCallback();
    }

    @Override
    AuthCredential extractFirebaseCredential(Object credentialProvider) {
        AccessToken token = (AccessToken) credentialProvider;
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        return FacebookAuthProvider.getCredential(token.getToken());
    }

    private void registerLoginCallback() {
        // Callback registration
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(mContext, "Login success!", Toast.LENGTH_SHORT).show();
                handleFirebaseSignIn(loginResult.getAccessToken());
                // App code
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(mContext, "Login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(mContext, "Login failed!", Toast.LENGTH_SHORT).show();
                // App code

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
