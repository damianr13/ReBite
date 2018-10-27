package rebite.ro.rebiteapp.persistence;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.PendingResult;

import rebite.ro.rebiteapp.users.GeneralProfileInfoProvider;
import rebite.ro.rebiteapp.users.UserInfo;

public class PersistenceManager {

    private static final String USERS_COLLECTION = "users";
        private static final String USER_ROLES_FIELD = "roles";

    private static final Object LOCK = new Object();

    private static PersistenceManager INSTANCE;

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = new PersistenceManager();
            }

            return INSTANCE;
        }
    }

    public void synchronizeUser(final Context context) {
        FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(GeneralProfileInfoProvider.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            return ;
                        }

                        persistUser(context);
                    }
                });
    }

    private void persistUser(final Context context) {
        UserInfo userInfo = new UserInfo(GeneralProfileInfoProvider.getInstance());

        FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(GeneralProfileInfoProvider.getInstance().getUid())
                .set(userInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context,
                                "Stored user into database!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void retrieveExtraUserInfo(final PendingResult.Callback<UserInfo> extraUserInfoCallback) {
        FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(GeneralProfileInfoProvider.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        boolean conditionsForNull = documentSnapshot == null ||
                                !documentSnapshot.exists() ||
                                !documentSnapshot.contains(USER_ROLES_FIELD);
                        if (conditionsForNull) {
                            extraUserInfoCallback.onResult(null);
                            return ;
                        }

                        extraUserInfoCallback.onResult(documentSnapshot.toObject(UserInfo.class));
                    }
                });
    }
}
