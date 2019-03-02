package rebite.ro.rebiteapp.persistence;

import android.content.Context;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UsersManager {
    public static final String TAG = UsersManager.class.getName();

    private static final Object LOCK = new Object();

    public static UsersManager INSTANCE;

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

    public void createNewUser(final Context context, String username, String password) {
        final FirebaseAuth auth = generateFirebaseInstance(context);
        auth.createUserWithEmailAndPassword(username, password)
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
                .setApiKey("AIzaSyAMHnNSE-hp0VenjCvzEBQUfSFSlkMsNw8")
                .setApplicationId("rebite-1538289789438").build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(context, firebaseOptions, "AnyAppName");
            return FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            return FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
        }
    }
}
