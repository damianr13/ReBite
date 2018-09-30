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

public abstract class AuthenticationProvider {

    private static final String TAG = AuthenticationProvider.class.getName();

    private LoginCallbacks mLoginCallbacks;
    private Context mContext;
    private FirebaseAuth mAuth;

    public AuthenticationProvider(Context context, LoginCallbacks loginCallbacks) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mLoginCallbacks = loginCallbacks;
    }

    abstract AuthCredential extractFirebaseCredential(Object credentialProvider);

    void handleFirebaseSignIn(Object credentialProvider) {
        AuthCredential credential = extractFirebaseCredential(credentialProvider);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new SignInCompleteListener());
    }

    private class SignInCompleteListener implements OnCompleteListener<AuthResult> {

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (!task.isSuccessful()) {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(mContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();

                return ;
            }

            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "signInWithCredential:success");
            mLoginCallbacks.updateUI();
        }
    }
}
