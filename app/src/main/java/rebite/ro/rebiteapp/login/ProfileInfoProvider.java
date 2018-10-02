package rebite.ro.rebiteapp.login;

import android.net.Uri;

public interface ProfileInfoProvider {
    public static final int PROFILE_PICTURE_WIDTH = 400;

    Uri getProfilePictureUri();
    String getDisplayName();
}
