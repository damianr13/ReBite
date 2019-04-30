package rebite.ro.rebiteapp.persistence;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import rebite.ro.rebiteapp.MainActivity;
import rebite.ro.rebiteapp.R;
import rebite.ro.rebiteapp.state.StateManager;
import rebite.ro.rebiteapp.users.GeneralProfileInfoProvider;

public class UsersManager {
    public static final String TAG = UsersManager.class.getName();

    private static final Object LOCK = new Object();

    private static UsersManager INSTANCE;

    private UsersManager(){

    }

    public static UsersManager getInstance() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = new UsersManager();
            }

            return INSTANCE;
        }
    }

    public Task<AuthResult> createNewUser(final Context context, String username, String password) {
        final FirebaseAuth auth = generateFirebaseInstance(context);
        return auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        Toast.makeText(context, "User successfully created",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(context, "Could not create user",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private FirebaseAuth generateFirebaseInstance(Context context) {
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://rebite-1538289789438.firebaseio.com")
                .setApiKey(context.getString(R.string.google_api_key))
                .setApplicationId("rebite-1538289789438").build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(context, firebaseOptions, "AnyAppName");
            return FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            return FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
        }
    }

    public void logOutCurrentUser() {
        FirebaseAuth.getInstance().signOut();
        GeneralProfileInfoProvider.getInstance().setProfileInfoProvider(null);
        StateManager.getInstance().setExtraUserInfo(null);
    }
}
