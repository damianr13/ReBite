package rebite.ro.rebiteapp.users;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class ProfileInfoProvider {
    protected int PROFILE_PICTURE_WIDTH = 400;

    public abstract Uri getProfilePictureUri();
    public abstract String getDisplayName();
    public abstract String getEmail();

    public String getUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        return currentUser.getUid();
    }
}
