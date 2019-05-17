package rebite.ro.rebiteapp.persistence;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.PendingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rebite.ro.rebiteapp.offers.RestaurantOffer;
import rebite.ro.rebiteapp.persistence.callbacks.OfferUpdatedCallback;
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

    private boolean cacheValid;

    private PersistenceManager() {
        cacheValid = false;
    }

    public static PersistenceManager getInstance() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = new PersistenceManager();
            }

            return INSTANCE;
        }
    }

    public void invalidateCache() {
        cacheValid = false;
    }

    public boolean isCacheValid() {
        return cacheValid;
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

    public void retrieveSpecificOffer(String documentId, RestaurantOffersRetrieverCallbacks callbacks) {
        FirebaseFirestore.getInstance()
                .collection(OFFERS_COLLECTION)
                .document(documentId)
                .get()
                .addOnCompleteListener(doc -> {
                    DocumentSnapshot documentSnapshot = doc.getResult();
                    if (documentSnapshot == null) {
                        return ;
                    }

                    RestaurantOffer targetOffer = documentSnapshot.toObject(RestaurantOffer.class);
                    syncAssignedUserFor(documentSnapshot, targetOffer, () ->
                            callbacks.onRestaurantOffersRetrieved(Collections.singletonList(targetOffer)));
                });
    }

    public void retrieveAllAvailableOffers(final RestaurantOffersRetrieverCallbacks callbacks) {
        Query q = FirebaseFirestore.getInstance()
                .collection(OFFERS_COLLECTION)
                .whereEqualTo(RestaurantOffer.STATE_FIELD, RestaurantOffer.OfferState.AVAILABLE)
                .whereGreaterThan(RestaurantOffer.PICK_UP_TIME_FIELD, System.currentTimeMillis());
        retrieveOffers(q, callbacks);
    }

    public void retrieveInProgressOffersForUser(final RestaurantOffersRetrieverCallbacks callbacks) {
        DocumentReference currentUserReference = FirebaseFirestore.getInstance()
                .collection(USERS_COLLECTION)
                .document(GeneralProfileInfoProvider.getInstance().getUid());
        Query q = FirebaseFirestore.getInstance()
                .collection(OFFERS_COLLECTION)
                .whereEqualTo(RestaurantOffer.STATE_FIELD, RestaurantOffer.OfferState.IN_PROGRESS)
                .whereEqualTo(RestaurantOffer.ASSIGNED_USER_FIELD, currentUserReference);
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
            if (verifyTaskFailed(task)) {
                    return ;
                }
                if (task.getResult() == null) {
                    return ;
                }

                List<RestaurantOffer> result = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    RestaurantOffer currentOffer = document.toObject(RestaurantOffer.class);
                    syncAssignedUserFor(document, currentOffer);
                    result.add(currentOffer);
                }

                callbacks.onRestaurantOffersRetrieved(result);
            });
        cacheValid = true;
    }

    private void syncAssignedUserFor(DocumentSnapshot offerDocument, RestaurantOffer offerObject,
                                     Runnable callback) {
        DocumentReference userReference =
                offerDocument.getDocumentReference(RestaurantOffer.ASSIGNED_USER_FIELD);
        if (userReference == null) {
            return ;
        }

        userReference.get()
                .addOnCompleteListener(task -> {
                    if(verifyTaskFailed(task)) {
                        return ;
                    }
                    if (task.getResult() == null) {
                        return ;
                    }

                    offerObject.assignedUser = task.getResult().toObject(UserInfo.class);
                    callback.run();
                });
    }

    private void syncAssignedUserFor(DocumentSnapshot offerDocument, RestaurantOffer offerObject) {
        syncAssignedUserFor(offerDocument, offerObject, () -> {});
    }

    private boolean verifyTaskFailed(Task task) {
        if (!task.isSuccessful()) {
            Log.d(TAG, "get failed with ", task.getException());
            return true;
        }

        return false;
    }

    public void updateDebugPickupTimes() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(OFFERS_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        DocumentReference ref = db.collection(OFFERS_COLLECTION)
                                .document(doc.getId());
                        long newDebugDeadline =
                                System.currentTimeMillis() + TimeUnit.HOURS.toMillis(13);
                        ref.update(RestaurantOffer.PICK_UP_TIME_FIELD, newDebugDeadline);
                    }
                });
    }

    public void assignOfferToCurrentUser(RestaurantOffer offer, OfferUpdatedCallback callback) {
        runUpdateQueryOnOffer(offer, callback,
                (documentReference) -> assignOfferToCurrentUser(documentReference, callback));
    }

    private void assignOfferToCurrentUser(DocumentReference offerReference,
                                          OfferUpdatedCallback callback) {
        DocumentReference userReference = FirebaseFirestore.getInstance()
                .collection(USERS_COLLECTION)
                .document(GeneralProfileInfoProvider.getInstance().getUid());
        Task<Void> updateTask = offerReference.update(
                RestaurantOffer.ASSIGNED_USER_FIELD, userReference,
                RestaurantOffer.STATE_FIELD, RestaurantOffer.OfferState.IN_PROGRESS);

        updateTask.addOnCompleteListener(task ->
                callback.onOfferUpdateFinished(!verifyTaskFailed(task)));
    }

    public void markOfferComplete(RestaurantOffer offer, OfferUpdatedCallback callback) {
        runUpdateQueryOnOffer(offer, callback,
                (offerReference) -> markOfferComplete(offerReference, callback));
    }

    private void markOfferComplete(DocumentReference offerReference, OfferUpdatedCallback callback) {
        offerReference.update(RestaurantOffer.STATE_FIELD, RestaurantOffer.OfferState.FINISHED)
                .addOnCompleteListener(
                        task -> callback.onOfferUpdateFinished(!verifyTaskFailed(task)));
    }

    private void runUpdateQueryOnOffer(RestaurantOffer offer, OfferUpdatedCallback callback,
                                       IOfferOperations operations) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(OFFERS_COLLECTION)
                .whereEqualTo(RestaurantOffer.RESTAURANT_INFO_FIELD, offer.restaurantInfo)
                .whereEqualTo(RestaurantOffer.PICK_UP_TIME_FIELD, offer.pickUpTimestamp)
                .get()
                .addOnCompleteListener(task -> {
                    if (verifyTaskFailed(task)) {
                        callback.onOfferUpdateFinished(false);
                        return ;
                    }
                    if (task.getResult() == null) {
                        callback.onOfferUpdateFinished(false);
                        return ;
                    }

                    DocumentSnapshot offerDocument = task.getResult().getDocuments().get(0);
                    DocumentReference offerReference = db.collection(OFFERS_COLLECTION)
                            .document(offerDocument.getId());
                    operations.apply(offerReference);
                });
    }

    private interface IOfferOperations {
        void apply(DocumentReference documentReference);
    }
}
