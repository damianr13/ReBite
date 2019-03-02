package rebite.ro.rebiteapp.login.email;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import rebite.ro.rebiteapp.login.AuthenticationProvider;
import rebite.ro.rebiteapp.login.LoginCallbacks;
import rebite.ro.rebiteapp.users.ProfileInfoProvider;

public class EmailAuthenticationProvider extends AuthenticationProvider<AuthCredential> {

    public EmailAuthenticationProvider(Context context, LoginCallbacks loginCallbacks) {
        super(context, loginCallbacks);
    }

    public void loginUserWithCredentials(@NonNull String username, @NonNull String password) {
        AuthCredential authCredential = EmailAuthProvider.getCredential(username, password);
        handleFirebaseSignIn(authCredential);
    }

    @Override
    protected AuthCredential extractFirebaseCredential(AuthCredential result) {
        return result;
    }

    @Override
    protected ProfileInfoProvider buildProfileInfoProvider(AuthCredential authCredential) {
        return new EmailProfileInfoProvider();
    }
}
