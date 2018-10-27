package rebite.ro.rebiteapp.login.facebook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;

import org.json.JSONObject;

import rebite.ro.rebiteapp.login.AuthenticationProvider;
import rebite.ro.rebiteapp.login.LoginCallbacks;
import rebite.ro.rebiteapp.users.ProfileInfoProvider;

public class FacebookAuthenticationProvider extends AuthenticationProvider<FacebookAuthInfoWrapper> {

    public static final String EMAIL_KEY = "email";
    private static final String FIELDS_KEY = "fields";

    private static final String TAG = FacebookAuthenticationProvider.class.getName();

    private CallbackManager mCallbackManager;
    private Context mContext;

    public FacebookAuthenticationProvider(Context context, LoginCallbacks loginCallbacks) {
        super(context, loginCallbacks);
        mContext = context;
        mCallbackManager = CallbackManager.Factory.create();

        registerLoginCallback();
    }

    @Override
    protected AuthCredential extractFirebaseCredential(FacebookAuthInfoWrapper credentialProvider) {
        AccessToken token = credentialProvider.getAccessToken();
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        return FacebookAuthProvider.getCredential(token.getToken());
    }

    @Override
    protected ProfileInfoProvider buildProfileInfoProvider(FacebookAuthInfoWrapper credentialProvider) {
        return new FacebookProfileInfoProvider(credentialProvider);
    }

    private void registerLoginCallback() {
        // Callback registration
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Toast.makeText(mContext, "Login success!", Toast.LENGTH_SHORT).show();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                handleFirebaseSignIn(new FacebookAuthInfoWrapper(
                                        loginResult.getAccessToken(),
                                        object));
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString(FIELDS_KEY, TextUtils.join(",", new String[] {
                        EMAIL_KEY
                }));
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext, "Login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(mContext, "Login failed!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
