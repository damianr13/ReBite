package rebite.ro.rebiteapp.persistence;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.PendingResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rebite.ro.rebiteapp.offers.RestaurantOffer;
import rebite.ro.rebiteapp.persistence.callbacks.RestaurantOffersRetrieverCallbacks;
import rebite.ro.rebiteapp.users.GeneralProfileInfoProvider;
import rebite.ro.rebiteapp.users.UserInfo;

public class PersistenceManager {

    private static final String TAG = PersistenceManager.class.getName();

    private static final String USERS_COLLECTION = "users";
    private static final String OFFERS_COLLECTION = "offers";

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

    public void synchronizeCurrentUser(final Context context) {
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

                        persistCurrentUser(context);
                    }
                });
    }

    private void persistCurrentUser(final Context context) {
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

    public void persistOfferToGlobalDatabase(final Context context, RestaurantOffer offer) {
        FirebaseFirestore.getInstance().collection(OFFERS_COLLECTION)
            .add(offer)
            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Toast.makeText(context, "Offer successfully published", Toast.LENGTH_SHORT)
                        .show();
                }
            });
    }

    public void retrieveAllAvailableOffers(final RestaurantOffersRetrieverCallbacks callbacks) {
        FirebaseFirestore.getInstance()
                .collection(OFFERS_COLLECTION)
                .whereGreaterThan(RestaurantOffer.PICK_UP_TIME_FIELD, Calendar.getInstance().getTimeInMillis())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!verifyTaskCompletition(task)) {
                            return ;
                        }
                        if (task.getResult() == null) {
                            return ;
                        }

                        List<RestaurantOffer> result = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            result.add(document.toObject(RestaurantOffer.class));
                        }

                        callbacks.onRestaurantOffersRetrieved(result);
                    }
                });
    }

    private boolean verifyTaskCompletition(Task task) {
        if (!task.isSuccessful()) {
            Log.d(TAG, "get failed with ", task.getException());
            return false;
        }

        return true;
    }
}
