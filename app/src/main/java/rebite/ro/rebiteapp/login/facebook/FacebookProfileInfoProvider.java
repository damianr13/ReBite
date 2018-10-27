package rebite.ro.rebiteapp.login.facebook;

import android.net.Uri;

import com.facebook.Profile;

import rebite.ro.rebiteapp.users.ProfileInfoProvider;

class FacebookProfileInfoProvider extends ProfileInfoProvider{

    private Profile mFacebookProfile;
    private FacebookAuthInfoWrapper mFacebookAuthInfoWrapper;

    FacebookProfileInfoProvider(FacebookAuthInfoWrapper facebookAuthInfoWrapper) {
        mFacebookProfile = Profile.getCurrentProfile();
        mFacebookAuthInfoWrapper = facebookAuthInfoWrapper;
    }

    @Override
    public Uri getProfilePictureUri() {
        return mFacebookProfile.getProfilePictureUri(PROFILE_PICTURE_WIDTH, 0);
    }

    @Override
    public String getDisplayName() {
        return mFacebookProfile.getName();
    }

    @Override
    public String getEmail() {
        return mFacebookAuthInfoWrapper.getEmail();
    }
}
