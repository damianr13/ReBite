package rebite.ro.rebiteapp.login.facebook;

import android.net.Uri;

import com.facebook.Profile;

import rebite.ro.rebiteapp.login.ProfileInfoProvider;

class FacebookProfileInfoProvider implements ProfileInfoProvider{

    private Profile facebookProfile;

    FacebookProfileInfoProvider() {
        facebookProfile = Profile.getCurrentProfile();
    }

    @Override
    public Uri getProfilePictureUri() {
        return facebookProfile.getProfilePictureUri(PROFILE_PICTURE_WIDTH, 0);
    }

    @Override
    public String getDisplayName() {
        return facebookProfile.getName();
    }
}
