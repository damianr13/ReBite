package rebite.ro.rebiteapp.login.google;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import rebite.ro.rebiteapp.login.ProfileInfoProvider;

class GoogleProfileInfoProvider implements ProfileInfoProvider {

    private GoogleSignInAccount googleAccount;

    GoogleProfileInfoProvider(Context context) {
        googleAccount = GoogleSignIn.getLastSignedInAccount(context);
    }

    @Override
    public Uri getProfilePictureUri() {
        Uri googlePhotoUri = googleAccount.getPhotoUrl();
        if (googlePhotoUri == null) {
            return null;
        }

        return getHigherResolutionImageUri(googlePhotoUri);
    }

    @Override
    public String getDisplayName() {
        return googleAccount.getDisplayName();
    }

    private Uri getHigherResolutionImageUri(@NonNull Uri uri) {
        String[] parts = uri.toString().split("/");
        parts[parts.length - 2] = "s" + PROFILE_PICTURE_WIDTH + "-c";

        return Uri.parse(TextUtils.join("/", parts));
    }
}
