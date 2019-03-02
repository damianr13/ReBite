package rebite.ro.rebiteapp.login;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import rebite.ro.rebiteapp.users.GeneralProfileInfoProvider;
import rebite.ro.rebiteapp.users.ProfileInfoProvider;

public abstract class AuthenticationProvider<T> {

    private static final String TAG = AuthenticationProvider.class.getName();

    private LoginCallbacks mLoginCallbacks;
    private Context mContext;
    private FirebaseAuth mAuth;

    public AuthenticationProvider(Context context, LoginCallbacks loginCallbacks) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mLoginCallbacks = loginCallbacks;
    }

    protected abstract AuthCredential extractFirebaseCredential(T credentialProvider);

    protected void handleFirebaseSignIn(T credentialProvider) {
        AuthCredential credential = extractFirebaseCredential(credentialProvider);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext,
                        new SignInCompleteListener(credentialProvider));
    }

    private class SignInCompleteListener implements OnCompleteListener<AuthResult> {

        private T mCredentialProvider;

        SignInCompleteListener(T credentialProvider) {
            mCredentialProvider = credentialProvider;
        }

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (!task.isSuccessful()) {
                mLoginCallbacks.onSignInFailed();
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(mContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();

                return ;
            }

            GeneralProfileInfoProvider.getInstance()
                    .setProfileInfoProvider(buildProfileInfoProvider(mCredentialProvider));

            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "signInWithCredential:success");
            mLoginCallbacks.onSignInComplete();
        }
    }

    protected abstract ProfileInfoProvider buildProfileInfoProvider(T credentialProvider);
}
