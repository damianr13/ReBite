package rebite.ro.rebiteapp.persistence;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.PendingResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rebite.ro.rebiteapp.offers.RestaurantOffer;
import rebite.ro.rebiteapp.persistence.callbacks.RestaurantOffersRetrieverCallbacks;
import rebite.ro.rebiteapp.users.GeneralProfileInfoProvider;
import rebite.ro.rebiteapp.users.UserInfo;
import rebite.ro.rebiteapp.users.restaurants.RestaurantInfo;

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
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        return ;
                    }

                    persistCurrentUser(context);
                });
    }

    private void persistCurrentUser(final Context context) {
        UserInfo userInfo = new UserInfo(GeneralProfileInfoProvider.getInstance());
        persistUserWithInfo(context, GeneralProfileInfoProvider.getInstance().getUid(),
                userInfo);
    }

    public void persistUserWithInfo(final Context context, String uid, UserInfo userInfo) {
        FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(uid)
                .set(userInfo)
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "Stored user into database!",
                        Toast.LENGTH_SHORT).show());
    }

    public void retrieveExtraUserInfo(final PendingResult.Callback<UserInfo> extraUserInfoCallback) {
        FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(GeneralProfileInfoProvider.getInstance().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    boolean conditionsForNull = documentSnapshot == null ||
                            !documentSnapshot.exists() ||
                            !documentSnapshot.contains(USER_ROLES_FIELD);
                    if (conditionsForNull) {
                        extraUserInfoCallback.onResult(null);
                        return ;
                    }

                    extraUserInfoCallback.onResult(documentSnapshot.toObject(UserInfo.class));
                });
    }

    public void persistOfferToGlobalDatabase(final Context context, RestaurantOffer offer) {
        FirebaseFirestore.getInstance().collection(OFFERS_COLLECTION)
            .add(offer)
            .addOnCompleteListener((task) ->
                Toast.makeText(context, "Offer successfully published", Toast.LENGTH_SHORT)
                        .show()

            );
    }

    public void retrieveAllAvailableOffers(final RestaurantOffersRetrieverCallbacks callbacks) {
        Query q = FirebaseFirestore.getInstance()
                .collection(OFFERS_COLLECTION)
                .whereGreaterThan(RestaurantOffer.PICK_UP_TIME_FIELD, Calendar.getInstance().getTimeInMillis());
        retrieveOffers(q, callbacks);
    }

    public void retrieveAllOffersByRestaurant(RestaurantInfo restaurantInfo,
                                              final RestaurantOffersRetrieverCallbacks callbacks) {
        Query q = FirebaseFirestore.getInstance()
                .collection(OFFERS_COLLECTION)
                .whereEqualTo(RestaurantOffer.RESTAURANT_INFO_FIELD, restaurantInfo);
        retrieveOffers(q, callbacks);
    }

    private void retrieveOffers(Query query, final RestaurantOffersRetrieverCallbacks callbacks) {
        query.get().addOnCompleteListener(task -> {
                    if (!verifyTaskCompletion(task)) {
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
                });
    }

    private boolean verifyTaskCompletion(Task task) {
        if (!task.isSuccessful()) {
            Log.d(TAG, "get failed with ", task.getException());
            return false;
        }

        return true;
    }
}
